package uk.ac.soton.ecs.aeg3g18.hybridimages;

import java.util.ArrayList;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.Gaussian2D;
import org.openimaj.image.processing.resize.ResizeProcessor;

public class MyHybridImages 
{
	/**
	 * Compute a hybrid image combining low-pass and high-pass filtered images
	 *
	 * @param lowImage
	 *            the image to which apply the low pass filter
	 * @param lowSigma
	 *            the standard deviation of the low-pass filter
	 * @param highImage
	 *            the image to which apply the high pass filter
	 * @param highSigma
	 *            the standard deviation of the low-pass component of computing the
	 *            high-pass filtered image
	 * @return the computed hybrid image
	 */
	public static MBFImage makeHybrid(MBFImage lowImage, float lowSigma, 
			MBFImage highImage, float highSigma) 
	{	
		//create respective gaussian convolvers with given sigma values
		MyConvolution convolverLow = new MyConvolution(Gaussian2D.createKernelImage(
				getGaussSize(lowSigma), lowSigma).pixels);
		MyConvolution convolverHigh = new MyConvolution(Gaussian2D.createKernelImage(
				getGaussSize(highSigma), highSigma).pixels);
		
		//generate filtered images
		lowImage = lowImage.process(convolverLow);
		highImage = highImage.subtract(highImage.process(convolverHigh));
		
		//add images to create output
		return lowImage.add(highImage);
	}
	
	//calculates size parameter for initialising gaussian kernel
	public static int getGaussSize(float sigma)
	{
		int size = (int) (8.0f * sigma + 1.0f);
		if (size % 2 == 0) size++;
		
		return size;
	}
	
	//outputs resized versions of image generated
	public static void displayHybrid(MBFImage lowImage, float lowSigma, 
			MBFImage highImage, float highSigma)
	{
		ArrayList<MBFImage> output = new ArrayList<MBFImage>();
		output.add(ResizeProcessor.halfSize(
				makeHybrid(lowImage, lowSigma, highImage, highSigma)));
		
		DisplayUtilities.createNamedWindow("output");
		
		for (int i = 1; i < 4; i++)
		{
			output.add(ResizeProcessor.halfSize(output.get(i - 1)));
		}
		DisplayUtilities.display("images", output);
	}
}