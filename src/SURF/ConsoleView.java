package SURF;

import java.util.LinkedList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.DMatch;
import org.bytedeco.javacpp.opencv_core.DMatchVector;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_features2d.*;


//import org.opencv.core.MatOfDMatch;
//import org.opencv.core.MatOfKeyPoint;
//import org.opencv.features2d.DMatch;

public class ConsoleView implements MatchingView{

	@Override
	public int showView(DMatchVector matches,KeyPointVector srcKP,KeyPointVector dstKP) {
		System.out.println(matches.size() + " Match Point(s)");

		double maxDist = Double.MIN_VALUE;
		double minDist = Double.MAX_VALUE;
//		DMatch d = new DMatch();
		
		
		
//		DMatch[] mats = matches.toArray();
		
//		for(int i = 0;i < mats.length;i++){
//			double dist = mats[i].distance;
//			if (dist < minDist) {
//				minDist = dist;
//			}
//			if (dist > maxDist) {
//				maxDist = dist;
//			}
//		}
		
		for(int i = 0;i < matches.size();i++){
			double dist = matches.get(i).distance();
			if (dist < minDist) {
				minDist = dist;
			}
			if (dist > maxDist) {
				maxDist = dist;
			}
		}
		
		System.out.println("Min Distance:" + minDist);
		System.out.println("Max Distance:" + maxDist);

		//将“好”的关键点记录，即距离小于3倍最小距离，同时给定一个阈值（0.2f），这样不至于在毫不相干的图像上分析，可依据实际情况调整
		List<DMatch> goodMatch = new LinkedList<DMatch>();
		
		for (int i = 0; i < matches.size(); i++) {
			double dist = matches.get(i).distance();
			if(dist <= 3*minDist&&dist < 0.2f){
//			if(dist <= 200f){
				goodMatch.add(matches.get(i));
			}
		}
		System.out.println(goodMatch.size() + " GoodMatch Found");
		int i = 0;
		for(DMatch ma:goodMatch){
			System.out.println("GoodMatch" + "["+i+"]:" + ma.queryIdx() + " TO: " + ma.trainIdx());
			i++;
		}
		
		
		
		return i;
	}

	@Override
	public void setDstPic(String dstPath) {}

	@Override
	public void setSrcPic(String picPath) {}
}
