package hog;

import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_highgui.*; 


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.PointVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_core.TermCriteria;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_ml;
import org.bytedeco.javacpp.helper.opencv_core;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacpp.opencv_ml.TrainData;
import org.bytedeco.javacpp.opencv_objdetect.HOGDescriptor;

public class Hog {

	private static final int width = 64;
	
	private static final int height = 128;

	public static void main(String[] args) {
		try {
			// List<Mat> mobs = new ArrayList<Mat>();

			List<Integer> trainingLabels = new ArrayList<Integer>();
			Mat trainingData = new Mat();
			Mat trainingLabel = new Mat();
			File dir = new File("lj");
			File[] files = dir.listFiles();
			Mat dataMat;
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				Mat img = imread(file.getPath());
				// MatExpr ones = Mat.ones(new Size(1, 1), CV_32FC1);
				// trainingLabels.push_back(new Mat(ones));
				Mat mat = new Mat();
				opencv_imgproc.resize(img, mat, new Size(width, height));
				opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);

				HOGDescriptor hDescriptor = new HOGDescriptor(new Size(width, height), new Size(16, 16), new Size(8, 8),
						new Size(8, 8), 9);
				FloatPointer fp = new FloatPointer();
				PointVector pv = new PointVector();
				hDescriptor.compute(mat, fp, new Size(1, 1), new Size(0, 0), pv);
				
				// if(i==0){
				// dataMat = new
				// Mat(Mat.zeros(files.length,hDescriptor.sizeof(),CV_32FC1));
				// }
				dataMat = new Mat(fp);
//				Mat m = new Mat();
//				opencv_features2d.drawKeypoints(img, new KeyPointVector(fp), m);
//				imshow("abc", m);
//				waitKey();

				System.out.println(dataMat.cols()+"--"+dataMat.rows());
				dataMat = dataMat.reshape(1, 1);
				System.out.println(dataMat.cols()+"--"+dataMat.rows());
				trainingData.push_back(dataMat);
//				trainingLabels.add(1);
				trainingLabel.push_back(new Mat(new int[]{1}));
				// trainingLabels.push_back();
			}

//			int[] labels = new int[trainingLabels.size()];
//			for (int i = 0; i < trainingLabels.size(); i++) {
//				labels[i] = trainingLabels.get(i);
//			}

//			Mat trainingLabel = new Mat(labels);
			
//			System.out.println(trainingLabel.rows()+";"+trainingLabel.cols());

			trainingData.convertTo(trainingData, CV_32F);

			SVM svm = SVM.create();
			svm.setType(SVM.ONE_CLASS);
			svm.setKernel(SVM.LINEAR);
			svm.setNu(0.6);
			svm.setTermCriteria(new TermCriteria(CV_TERMCRIT_ITER, 500, 1e-6));
			// svm.setGamma(50);
			// svm.train(trainingData, trainingLabels, new Mat());

			// svm.train(trainingData,opencv_ml.ROW_SAMPLE , trainingLabel);
			 System.out.println(trainingData.cols()+";"+trainingData.rows()+";"+CV_32F);
			TrainData td = TrainData.create(trainingData, opencv_ml.ROW_SAMPLE, trainingLabel);
			svm.trainAuto(td);

			svm.save("d:/svm_m");

			File dir2 = new File("a");
			Mat testMat;
			int total = 0;
			int lj = 0;
			int ljTrue = 0;
			int zc = 0;
			int zcTrue = 0;
			for (File file : dir2.listFiles()) {
				total++;
				Mat img = imread(file.getPath());
				// img = new Mat(1, 76800, CV_32FC1);

				Mat mat = new Mat();
				opencv_imgproc.resize(img, mat, new Size(width, height));

				HOGDescriptor hDescriptor = new HOGDescriptor(new Size(width, height), new Size(16, 16), new Size(8, 8),
						new Size(8, 8), 9);
				FloatPointer fp = new FloatPointer();
				hDescriptor.compute(mat, fp, new Size(1, 1), new Size(0, 0), new PointVector());
				// if(i==0){
				// dataMat = new
				// Mat(Mat.zeros(files.length,hDescriptor.sizeof(),CV_32FC1));
				// }
				testMat = new Mat(fp);

				opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
				testMat = testMat.reshape(1, 1);

				// imgMat.cols(76800);

				Mat sample = new Mat();
				testMat.convertTo(sample, CV_32F);
				 System.out.println(sample.cols()+";"+sample.rows()+";"+CV_32F);
				float res = svm.predict(sample);
				System.out.println(file.getName() + ": " + res);
//				if (file.getName().startsWith("b")) {
//					zcTrue++;
//					if (res == 0.0) {
//						zc++;
//					}
//				} else {
					ljTrue++;
					if (res == 1.0) {
						lj++;
					}
//				}
			}
			// System.out.println("垃圾率:"+(float)lj/total);
			System.out.println("垃圾图片正确率:" + (ljTrue==0?"无":(float) lj / ljTrue));
			System.out.println("正常图片正确率:" + (zcTrue==0?"无":(float) zc / zcTrue));

			// Mat trainingData = new Mat();

			// System.out.println(trainingData.cols());

			// CvSVMParams params = new CvSVMParams();
			// params.set_svm_type(SVM.ONE_CLASS);
			// params.set_kernel_type(SVM.RBF);

			// CvSVMParams params = new CvSVMParams();
			// params.svm_type(CvSVM.C_SVC);
			// params.kernel_type(CvSVM.LINEAR);
			// params.gamma(3);
			// params.degree(CvSVM.DEGREE);

			// params.set_nu(0.1);

			// int[] labels = new int[]{1,1,1,1,1,0,0,0};

			// svm.save("d:/imgsvm-model");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
