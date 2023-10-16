package uk.ac.soton.ecs.aeg3g18.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> 
{
	//NOTE: all pixel vals are represented as [y][x] indices
	
	//kernel and [x, y] index to its centre
	private float[][] kernel;
	private int[] kernelCentre;

	//constructor takes kernel; if kernel invalid (even height/width), set to null
	public MyConvolution(float[][] kernel) 
	{
		//kernel.length corresponds to y indices (row index)
		//kernel[n].length corresponds to x indices (column index)
		if (kernel.length % 2 != 0 && kernel[0].length % 2 != 0)
		{
			this.kernel = kernel;
			this.kernelCentre = new int[] { kernel.length / 2, 
					kernel[0].length / 2};
		}
		//invalidate kernels for which convolution function would fail
		else
		{
			this.kernel = null;
		}
	}

	@Override
	public void processImage(FImage image) 
	{
		// convolve image with kernel and store result back in image
		if (this.kernel != null)
		{
			//create zero-padded version of old image
			FImage padImage = image.padding(this.kernelCentre[1], 
					this.kernelCentre[0], 0f);
			
			//define dimensions for new convolved image
			float[][] newImage = new float[image.getHeight()][image.getWidth()];
			
			//each horizontal pass
			/* yPassInd = y axis index of top left kernel position relative to current
			 * vertical pass
			 */
			for (int yPassInd = 0; yPassInd < newImage.length; yPassInd++)
			{
				//each vertical pass
				/* xPassInd = x axis index of top left kernel position relative
				 * to current vertical pass 
				 */
				for (int xPassInd = 0; xPassInd < newImage[0].length; xPassInd++)
				{
					float newPixelVal = 0;
					
					//perform convolve using relative point
					for (int yKernel = 0; yKernel < kernel.length; yKernel++)
					{
						for (int xKernel = 0; xKernel < kernel[0].length; xKernel++)
						{
							newPixelVal += kernel[yKernel][xKernel] * 
									padImage.pixels[yPassInd + yKernel]
											[xPassInd + xKernel];
						}
					}
					newImage[yPassInd][xPassInd] = newPixelVal;
				}
			}
			
			image.internalAssign(new FImage(newImage));
		}
	}
}