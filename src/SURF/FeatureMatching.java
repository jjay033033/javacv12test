package SURF;

import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;


import org.bytedeco.javacpp.opencv_core.DMatchVector;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_features2d.BFMatcher;
import org.bytedeco.javacpp.opencv_features2d.BOWImgDescriptorExtractor;
import org.bytedeco.javacpp.opencv_features2d.BOWKMeansTrainer;
import org.bytedeco.javacpp.opencv_features2d.DescriptorMatcher;
import org.bytedeco.javacpp.opencv_features2d.FastFeatureDetector;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_xfeatures2d.BriefDescriptorExtractor;
import org.bytedeco.javacpp.opencv_xfeatures2d.SURF;  

//import org.opencv.core.Mat;
//import org.opencv.core.MatOfDMatch;
//import org.opencv.core.MatOfKeyPoint;
//import org.opencv.features2d.DescriptorExtractor;
//import org.opencv.features2d.DescriptorMatcher;
//import org.opencv.features2d.FeatureDetector;
//import org.opencv.highgui.Highgui;

//import com.thrblock.opencv.fm.view.ConsoleView;
//import com.thrblock.opencv.fm.view.MatchingView;

public class FeatureMatching {
	private Mat src;
	private KeyPointVector srcKeyPoints;
	private Mat srcDes;
	
	private FastFeatureDetector detector;
	private BriefDescriptorExtractor extractor;
	private DescriptorMatcher matcher;
	
	private SURF surf = SURF.create();

	private MatchingView view;
	public FeatureMatching(MatchingView view) {
		this.view = view;
		srcKeyPoints = new KeyPointVector();
		srcDes = new Mat();
		detector = FastFeatureDetector.create();
		matcher = new BFMatcher();
//		matcher = new FlannBasedMatcher();
		extractor = BriefDescriptorExtractor.create();
		

//		b.cluster(new );
		
//		descriptorextractor
	}

	public int doMaping(String dstPath) {
		view.setDstPic(dstPath);
		// 读入待测图像
		Mat dst = imread(dstPath);
		opencv_imgproc.cvtColor(dst, dst, opencv_imgproc.COLOR_BGR2GRAY);
		System.out.println("DST W:"+dst.cols()+" H:" + dst.rows());
		// 待测图像的关键点
		KeyPointVector dstKeyPoints = new KeyPointVector();
		surf.detect(dst, dstKeyPoints);
		// 待测图像的特征矩阵
		Mat dstDes = new Mat();
		surf.compute(dst, dstKeyPoints, dstDes);
		// 与原图匹配
		
		DMatchVector matches = new DMatchVector();
		dstDes.convertTo(dstDes, CV_32F);
		srcDes.convertTo(srcDes, CV_32F);
		matcher.match( srcDes,dstDes, matches);
		
//		System.out.println(matches.isNull());
		
		Mat d = new Mat();
//		d.convertTo(d, CV_32F);
		
//		opencv_imgproc.resize(src, src, new Size(300, 300));
//		opencv_imgproc.resize(dst, dst, new Size(300, 300));
		System.out.println(srcKeyPoints.size());
		System.out.println(dstKeyPoints.size());
		System.out.println(matches.size() + " Match Point(s)");
		opencv_features2d.drawMatches(src, srcKeyPoints, dst, dstKeyPoints, matches, d);
		imshow("abc", d);
		waitKey();
		//将结果输入到视图 并得到“匹配度”
		return view.showView(matches, srcKeyPoints, dstKeyPoints);
		
//		return 0;
	}

	public void setSource(String srcPath) {
		view.setSrcPic(srcPath);
		// 读取图像 写入矩阵
		src = imread(srcPath);
		opencv_imgproc.cvtColor(src, src, opencv_imgproc.COLOR_BGR2GRAY);
		System.out.println("SRC W:"+src.cols()+" H:" + src.rows());
		// 检测关键点
		surf.detect(src, srcKeyPoints);
		// 根据源图像、关键点产生特征矩阵数值
		surf.compute(src, srcKeyPoints, srcDes);
	}

	public static void main(String[] args) {
//		System.loadLibrary("opencv_java249");
		FeatureMatching mather = new FeatureMatching(new ConsoleView());
		//FeatureMatching mather = new FeatureMatching(new GEivView());
		mather.setSource("./surf/6.jpg");
		mather.doMaping("./surf/3.jpg");
	}
}
