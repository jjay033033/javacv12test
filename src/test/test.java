/**
 * 
 */
package test;

import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatExpr;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacpp.opencv_imgproc;

/**
 * @author guozy
 * @date 2016-8-15
 * 
 */
public class test {

	public static void main(String[] args) {
//		Mat mat = imread("surf/3.jpg");
//		Mat mat2 = new Mat();
//		opencv_imgproc.resize(mat, mat2, new Size(1000, 500));
//		imshow("a", mat);
//		imshow("b", mat2);
//		SVM svm = new SVM(null);
//
//		waitKey();
//		opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
//		imwrite("surf/4.jpg", mat);
		

		Mat mat = new Mat(1,2,3,4,5,6);
//		mat = mat.reshape(0, 3);
//
//		System.out.println(mat.rows()+":"+mat.cols());
		System.out.println(mat);

	}

	public static void doSomething() {
		// 读取图像
		String imagePath = "file/1.png";
		Mat img = imread(imagePath);
		if (img != null) {
			imshow("javacv1.1", img);
			waitKey();
		} else {
			System.out.println("无法加载图像");
		}
	}

}
