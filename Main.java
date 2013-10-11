import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class Main {
	public static void main(String[] args) {
		
		System.out.println("Stegabram!!");
		
		File inputFile = new File(args[0]);
		File keyFile = new File(args[1]);
		File outputFile = new File(args[2]);
		
		boolean encrypt = true;//args[3].equals("-e");
		
		try {
			if (encrypt) {
				encrypt(inputFile, keyFile, outputFile);
			} else {
				decrypt(outputFile, keyFile, new File("/Users/brambuurlage/Desktop/hoi.pdf"));
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private static void encrypt(File inputFile, File keyFile, File outputFile) throws IOException {
		FileInputStream in = new FileInputStream(inputFile);
		byte[] input = new byte[(int) (inputFile.length())];
		
		for (int i=0; i<inputFile.length(); i+=4096) {
			in.read(input, i, (int)Math.min(4096, inputFile.length()-i));
		}
		in.close();
		
		BufferedImage img   = ImageIO.read(keyFile);
		long  bitsToEncrypt   = (inputFile.length()+4)*8;
		long  pixelsAvailable = (img.getWidth()*img.getHeight()) - 2;
		int   bitsPerPixel    = (int)Math.ceil(bitsToEncrypt/(float)pixelsAvailable); // the amount of bits we have to store per pixel to store the input evenly over the whole image
		int[] bitsPerChannel  = new int[] {0,0,0};
		int p;
		
		for (int i=0; i<bitsPerPixel; ++i) bitsPerChannel[i%3]++;
		
		int next_input_bit = 0;		// offset in bits to start at with the next pixel
		int next_image_pixel = 2;	// the index of the pixel where we store the next bits
		
		/**
		 * Step 1: In the first pixel we will flip the bits that will be replaced in the other pixels
		 * 		   this way we can know where to look for data.
		 */
		p = img.getRGB(0, 0);
		byte[] first_pixel = new byte[] {
			(byte)((p >> 16) & 0xff), 
			(byte)((p >>  8) & 0xff), 
			(byte)((p)       & 0xff),	
			(byte)((p >> 24) & 0xff),
		};
		
		setChannelEncoding(first_pixel, bitsPerChannel);
		img.setRGB(1, 0, p);
		img.setRGB(0, 0, 
				(first_pixel[3] << 24) |
				(first_pixel[0] << 16) |
				(first_pixel[1] <<  8) |
				(first_pixel[2] <<  0));
		
		/**
		 * Step 2: Concat the length and the data
		 */
		ByteBuffer buf = ByteBuffer.allocate(4 + input.length);
		buf.putInt(input.length);
		buf.put(input);
		buf.rewind();
		input = buf.array();
		
		System.out.println("bits per pixel: " + bitsPerPixel + ":" + bitsPerChannel[0] + "," + bitsPerChannel[1] + "," + bitsPerChannel[2]);
		System.out.println("total bytes: " + input.length);
		
		/**
		 * Step 3: Replace pixels with data :)
		 */
		while (next_input_bit < bitsToEncrypt) {
			p = img.getRGB(next_image_pixel%img.getWidth(), next_image_pixel/img.getWidth());
			byte[] pixel = new byte[] {
				(byte)((p >> 16) & 0x00), 
				(byte)((p >>  8) & 0x00), 
				(byte)((p >>  0) & 0x00),
				
				(byte)((p >> 24) & 0xff),
			};
			
			for (int c=0; c<3; ++c) {
				for (int b=0; b<bitsPerChannel[c]; ++b) {
					byte current = (byte) (input[next_input_bit/8]);
					
					int org_bit = ((pixel[c]>>b)&0x01);
					int new_bit = ((current>>next_input_bit)&0x01);
					pixel[c] ^= (org_bit ^ new_bit) << (b+4);
					
					next_input_bit++;
					if (next_input_bit >= bitsToEncrypt) {
						break;
					}
				}
				
				if (next_input_bit >= bitsToEncrypt) {
					break;
				}
			}
	
			img.setRGB(next_image_pixel%img.getWidth(), next_image_pixel/img.getWidth(),
					(pixel[3] << 24) |
					(pixel[0] << 16) |
					(pixel[1] <<  8) |
					(pixel[2] <<  0));
			next_image_pixel++;
		}
		
		/**
		 * Step 4: Write the results to a file
		 */
		outputFile.createNewFile();
		ImageIO.write(img, "png", outputFile);
	}

	private static void decrypt(File inputFile, File keyFile, File outputFile) throws IOException {
		BufferedImage img   = ImageIO.read(inputFile);
		int p1,p2;
		
		/**
		 * Step 1: do the stuff
		 */
		p1 = img.getRGB(0, 0);
		p2 = img.getRGB(1, 0);
		
		int[] bitsPerChannel = new int[3];
		getChannelEncoding(new byte[] { (byte)((p1 >> 16) & 0xff), 
										(byte)((p1 >>  8) & 0xff), 
										(byte)((p1)       & 0xff) }, 
						   new byte[] { (byte)((p2 >> 16) & 0xff), 
										(byte)((p2 >>  8) & 0xff), 
										(byte)((p2)       & 0xff) }, 
										bitsPerChannel);
		
		int next_image_pixel = 2;
		int next_output_bit = 0;
		int written_bytes = 0;
		int size = -4;
		boolean done = false;
		FileOutputStream out = new FileOutputStream(outputFile);
		ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
		
		byte current = 0;
		
		while (written_bytes < size || size < 0) {
			p1 = img.getRGB(next_image_pixel%img.getWidth(), next_image_pixel/img.getWidth());
			next_image_pixel++;
			
			byte[] pixel = new byte[] {
				(byte)((p1 >> 16) & 0xff), 
				(byte)((p1 >>  8) & 0xff), 
				(byte)((p1)       & 0xff),
			};
			
			for (int c=0; c<3; ++c) {
				for (int b=0; b<bitsPerChannel[c]; ++b) {
					
					System.out.println((((pixel[c] >> b)&0x01) << next_output_bit));
					
					current |= (((pixel[c] >> b)&0x01) << next_output_bit);
					if (next_output_bit == 7) {
						next_output_bit = 0;
						
						if (size < 0) {
							lengthBuffer.put(current);
							size ++;
							
							//expected = 2491693
							if (size == 0) {
								lengthBuffer.rewind();
								size = lengthBuffer.getInt();
								
								System.out.println("found size: " + size);
							}
						} else {
							out.write((int)current);
							written_bytes ++;
						}
						
						current = 0x0;
					} else {
						next_output_bit ++;
					}
				}
			}
		}
		
		out.close();
	}
	
	private static void getChannelEncoding(byte[] pixel1, byte[] pixel2, int[] channels) {
		for (int c=0; c<3; ++c) {
			channels[c] = 0;
			for (int b=0; b<8; ++b) {
				byte p1 = (byte) ((pixel1[c] >> b)&0x01);
				byte p2 = (byte) ((pixel2[c] >> b)&0x01);
				
				if (p1 != p2) channels[c]++;
			}
		}
	}
	
	private static void setChannelEncoding(byte[] pixel, int[] channels) {
		for (int c=0; c<3; ++c) {
			for (int b=0; b<channels[c]; ++b) {
				pixel[c] ^= (0x01 << b);
			}
		}
	}
}