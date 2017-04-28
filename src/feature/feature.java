package feature;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;

import java.io.UnsupportedEncodingException;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.FileNode;
import org.bytedeco.javacpp.opencv_core.FileNodeIterator;
import org.bytedeco.javacpp.opencv_core.FileNodeIterator.SeqReader;
import org.bytedeco.javacpp.opencv_core.FileStorage;
import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

public class feature {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println("wwwwwwaaaa");
		System.out.println(opencv_core.CV_VERSION());
		Mat c1 = imread("res/210949924.jpg");
		CvMat c2 = new CvMat(c1);

		FileStorage fs = new FileStorage("res/vocabulary.xml",FileStorage.WRITE);
		if(fs.isOpened()){
			fs.writeObj("vocabulary", c2);
			fs.release();
		}
		System.out.println(FileStorage.getDefaultObjectName("vocabulary.xml"));
//		FileStorage fs = new FileStorage("vocabulary.fs",FileStorage.READ);
//		Mat a = new Mat();
//		CvMat b = new CvMat();
//		FileNode fn = null;
//		Pointer p = null;
//		if(fs.isOpened()){
//			
//			fn = fs.get("vocabulary");
//			b = new CvMat(fn.readObj());
//
//			imshow("a", new Mat(b));
//			waitKey();
//			fs.release();
//		}
		
	

		
//		Mat c2 = imread("kf2.png");
		

//		Mat f1 = new Mat();
//		Mat f2 = new Mat();
//
//		opencv_imgproc.cvtColor(c1, f1, opencv_imgproc.COLOR_BGR2GRAY);
//		opencv_imgproc.cvtColor(c2, f2, opencv_imgproc.COLOR_BGR2GRAY);
//
//		FastFeatureDetector detector = FastFeatureDetector.create();
//		BOWImgDescriptorExtractor extractor = new BOWImgDescriptorExtractor(null);
//
//		KeyPointVector mkpf1 = new KeyPointVector();
//		KeyPointVector mkpf2 = new KeyPointVector();
//		Mat df1 = new Mat();
//		Mat df2 = new Mat();
//
//		detector.detect(f1, mkpf1);
//		detector.detect(f2, mkpf2);
//
////		System.out.println("Keypoints detected for f2: " + mkpf2.total());
////		System.out.println("Keypoints detected for f1: " + mkpf1.total());
//
//		extractor.compute(f1, mkpf1, df1);
//		extractor.compute(f2, mkpf2, df2);
//
////		System.out.println("Keypoints after extractor for f2: " + mkpf2.rows());
////		System.out.println("Keypoints after extractor for f1: " + mkpf1.rows());
	}
	
}
