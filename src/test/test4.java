package test;

import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_core.TermCriteria;
import org.bytedeco.javacpp.opencv_features2d.BOWImgDescriptorExtractor;
import org.bytedeco.javacpp.opencv_features2d.BOWKMeansTrainer;
import org.bytedeco.javacpp.opencv_features2d.DescriptorMatcher;
import org.bytedeco.javacpp.opencv_features2d.FastFeatureDetector;
import org.bytedeco.javacpp.opencv_features2d.FlannBasedMatcher;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_ml;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacpp.opencv_ml.TrainData;
import org.bytedeco.javacpp.opencv_xfeatures2d.BriefDescriptorExtractor;
import org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;

public class test4 {

	private static final int size = 160;
	
	private static Mat vocabulary;
	
	private static DescriptorMatcher matcher= new FlannBasedMatcher();
	
	public static BOWImgDescriptorExtractor bowDescriptorExtractor = new BOWImgDescriptorExtractor(matcher);
	
	private static Mat features1 = new Mat();

	private static Mat features2 = new Mat();
	
	private static BOWKMeansTrainer bowTrainer = new BOWKMeansTrainer(100);
	
	public static SIFT sift = SIFT.create();
	
	public static FastFeatureDetector detector = FastFeatureDetector.create();
	private static BriefDescriptorExtractor extractor = BriefDescriptorExtractor.create();

	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		// System.loadLibrary("opencv_java2413");

		// System.out.println(org.bytedeco.javacpp.opencv_core.CV_VERSION());;
		SVM train = train();
//		SVM train = trainBow();

		// train = train("b",0);

//		 SVM train = SVM.loadSVM("d:/imgsvm-model", "");
//		SVM train = new SVM(new Pointer());
//		train = train.loadSVM("d:/imgsvm-model", "");

		File dir = new File("d:/aaa");
		int total = 0;
		int lj = 0;
		int ljTrue = 0;
		int zc = 0;
		int zcTrue = 0;
		for (File file : dir.listFiles()) {
			total++;
			Mat img = imread(file.getPath());

			Mat mat = new Mat();
			opencv_imgproc.resize(img, mat, new Size(size, size));
			opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
			Mat imgMat = mat.reshape(1, 1);


			Mat sample = new Mat();
			imgMat.convertTo(sample, CV_32FC1);
			// System.out.println(sample.cols()+";"+sample.type()+";"+CV_32FC1);
			float res = train.predict(sample);
			
			System.out.println(file.getName() + ": " + res);

//			if (file.getName().startsWith("b")) {
//				zcTrue++;
//				if (res == 0.0) {
//					zc++;
//				}
//			} else {
//				ljTrue++;
//				if (res == 1.0) {
//					lj++;
//				}
//			}
		}
		// System.out.println("垃圾率:"+(float)lj/total);
		System.out.println("垃圾图片正确率:" + (ljTrue==0?"无":(float) lj / ljTrue));
		System.out.println("正常图片正确率:" + (zcTrue==0?"无":(float) zc / zcTrue));
	}

	private static SVM train() {
		Mat trainingData = new Mat();
		List<Integer> trainingLabelsList = new ArrayList<Integer>();
		train(trainingData, trainingLabelsList, "2dbc", 1);
		train(trainingData, trainingLabelsList, "politics", 2);
		train(trainingData, trainingLabelsList, "ad", 3);

		train(trainingData, trainingLabelsList, "norm", 0);
		
//		train(trainingData, trainingLabelsList, "zz", 1);
//		train(trainingData, trainingLabelsList, "gg", 2);
//		train(trainingData, trainingLabelsList, "qr", 3);
//		train(trainingData, trainingLabelsList, "new", 4);
		SVM svm = train(trainingData, trainingLabelsList);
		svm.save("d:/imgsvm-model");
		return svm;
	}

	private static SVM train2() {
		Mat trainingData = new Mat();
		List<Integer> trainingLabelsList = new ArrayList<Integer>();
		train(trainingData, trainingLabelsList, "lj", 1);
		// train(trainingData, trainingLabelsList, "b", 0);
		SVM svm = train2(trainingData, trainingLabelsList);
		svm.save("d:/imgsvm-model2");
		return svm;
	}
	
	private static SVM trainBow() {
		Mat trainingData = new Mat();
		List<Integer> trainingLabelsList = new ArrayList<Integer>();
		trainBow(trainingData, trainingLabelsList, "lj", 1);
		trainBow(trainingData, trainingLabelsList, "new", 0);
		vocabulary = bowTrainer.cluster();
		bowDescriptorExtractor.setVocabulary(vocabulary);
//		train(trainingData, trainingLabelsList, "zz", 1);
//		train(trainingData, trainingLabelsList, "gg", 2);
//		train(trainingData, trainingLabelsList, "qr", 3);
//		train(trainingData, trainingLabelsList, "new", 4);
		SVM svm = trainBow(trainingData, trainingLabelsList);
		svm.save("d:/imgsvm-model-b");
		return svm;
	}

	private static void train(Mat trainingData, List<Integer> trainingLabels, String filename, int label) {
		// float[] labels = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
		// List<Integer> list = new ArrayList<Integer>();

		// Mat trainingImages = new Mat();
		try {
			// List<Mat> mobs = new ArrayList<Mat>();

			File dir = new File(filename);
			for (File file : dir.listFiles()) {
				Mat img = imread(file.getPath());
				// MatExpr ones = Mat.ones(new Size(1, 1), CV_32FC1);
				// trainingLabels.push_back(new Mat(ones));
				Mat mat = new Mat();
				opencv_imgproc.resize(img, mat, new Size(size, size));
				opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
				mat = mat.reshape(1, 1);

				trainingData.push_back(mat);
				trainingLabels.add(label);
				// trainingLabels.push_back();
			}

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
	
	private static void trainBow(Mat trainingData, List<Integer> trainingLabels, String filename, int label) {
		// float[] labels = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
		// List<Integer> list = new ArrayList<Integer>();

		// Mat trainingImages = new Mat();
		try {
			// List<Mat> mobs = new ArrayList<Mat>();

			
			
			
			Mat clusterData = new Mat();
			
			File dir = new File(filename);
			for (File file : dir.listFiles()) {
				Mat img = imread(file.getPath());
				Mat mat = new Mat();
//				opencv_imgproc.resize(img, mat, new Size(size, size));
				opencv_imgproc.cvtColor(img, mat, opencv_imgproc.COLOR_BGR2GRAY);
//				mat = mat.reshape(1, 1);
				KeyPointVector dstKeyPoints = new KeyPointVector();
				sift.detect(mat, dstKeyPoints);
				Mat imageDescriptor = new Mat();
				sift.compute(mat, dstKeyPoints, features1);
//				clusterData.push_back(features1);
				
				bowDescriptorExtractor.compute(mat, dstKeyPoints, imageDescriptor);
				trainingData.push_back(imageDescriptor);
				trainingLabels.add(label);
				// trainingLabels.push_back();
				bowTrainer.add(features1);
			}
			
			
			

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

	private static SVM train(Mat trainingData, List<Integer> trainingLabelsList) {
		int[] labels = new int[trainingLabelsList.size()];
		for (int i = 0; i < trainingLabelsList.size(); i++) {
			labels[i] = trainingLabelsList.get(i);
		}

		Mat trainingLabels = new Mat(labels);

		trainingData.convertTo(trainingData, CV_32FC1);
		return train(trainingData, trainingLabels);
	}

	private static SVM train2(Mat trainingData, List<Integer> trainingLabelsList) {
		int[] labels = new int[trainingLabelsList.size()];
		for (int i = 0; i < trainingLabelsList.size(); i++) {
			labels[i] = trainingLabelsList.get(i);
		}

		Mat trainingLabels = new Mat(labels);

		trainingData.convertTo(trainingData, CV_32FC1);
		return train2(trainingData, trainingLabels);
	}
	
	private static SVM trainBow(Mat trainingData, List<Integer> trainingLabelsList) {
		int[] labels = new int[trainingLabelsList.size()];
		for (int i = 0; i < trainingLabelsList.size(); i++) {
			labels[i] = trainingLabelsList.get(i);
		}

		Mat trainingLabels = new Mat(labels);

		trainingData.convertTo(trainingData, CV_32FC1);
		return trainBow(trainingData, trainingLabels);
	}

	private static SVM train(Mat trainingData, Mat trainingLabels) {
		SVM svm = SVM.create();
		svm.setType(SVM.C_SVC);
		svm.setKernel(SVM.LINEAR);
//		svm.setGamma(10000000);
//		 svm.setC(0.00000001);
//		 
//		 Mat mat = new Mat(new FloatPointer(3f,1f));
//
//		 svm.setClassWeights(mat);
		// svm.setNu(0.8);
		// svm.train(trainingData, trainingLabels, new Mat());
		svm.train(trainingData, opencv_ml.ROW_SAMPLE, trainingLabels);
		// TrainData td = TrainData.create(trainingData, opencv_ml.ROW_SAMPLE,
		// trainingLabels);
		// svm.trainAuto(td);
		return svm;
	}

	private static SVM train2(Mat trainingData, Mat trainingLabels) {
		SVM svm = SVM.create();
		svm.setType(SVM.ONE_CLASS);
		svm.setKernel(SVM.LINEAR);
//		svm.setNu(0.453);
		svm.setNu(0.3);
		svm.setTermCriteria(new TermCriteria(CV_TERMCRIT_ITER, 500, 1e-6));
//		 svm.setGamma(0.00000001);
		// svm.train(trainingData, trainingLabels, new Mat());
		// svm.train(trainingData,opencv_ml.ROW_SAMPLE , trainingLabels);
		TrainData td = TrainData.create(trainingData, opencv_ml.ROW_SAMPLE, trainingLabels);
		svm.trainAuto(td);
		return svm;
	}
	
	private static SVM trainBow(Mat trainingData, Mat trainingLabels) {
		SVM svm = (SVM) SVM.create();
		svm.setType(SVM.C_SVC);
		svm.setKernel(SVM.LINEAR);
		svm.setTermCriteria(new TermCriteria(CV_TERMCRIT_ITER, 100, 1e-6));

//		svm.setGamma(10000000);
//		 svm.setC(0.00000001);
//		 
//		 Mat mat = new Mat(new FloatPointer(3f,1f));
//
//		 svm.setClassWeights(mat);
		// svm.setNu(0.8);
		// svm.train(trainingData, trainingLabels, new Mat());
		svm.train(trainingData, opencv_ml.ROW_SAMPLE, trainingLabels);
		// TrainData td = TrainData.create(trainingData, opencv_ml.ROW_SAMPLE,
		// trainingLabels);
		// svm.trainAuto(td);
		return svm;
	}

}
