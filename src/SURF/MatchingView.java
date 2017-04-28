package SURF;

import org.bytedeco.javacpp.opencv_core.DMatchVector;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;

//import org.opencv.core.MatOfDMatch;
//import org.opencv.core.MatOfKeyPoint;


public interface MatchingView {
	public void setDstPic(String dstPath);
	public void setSrcPic(String picPath);
	public int showView(DMatchVector matches,KeyPointVector srcKP,KeyPointVector dstKP);
}
