package Bow;

import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.FileNode;
import org.bytedeco.javacpp.opencv_core.FileStorage;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.TermCriteria;
import org.bytedeco.javacpp.opencv_features2d.BOWImgDescriptorExtractor;
import org.bytedeco.javacpp.opencv_features2d.BOWKMeansTrainer;
import org.bytedeco.javacpp.opencv_features2d.DescriptorMatcher;
import org.bytedeco.javacpp.opencv_features2d.FlannBasedMatcher;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_ml;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;

import util.TimeUtil;



public class BowSift {
	
	private static final int size = 160;
	
	private static Mat vocabulary;
	
	private static DescriptorMatcher matcher= new FlannBasedMatcher();
	
	public static SIFT sift = SIFT.create();
	
	public static BOWImgDescriptorExtractor bowDescriptorExtractor = new BOWImgDescriptorExtractor(sift,matcher);
	
//	private static Mat features1 = new Mat();

//	private static Mat features2 = new Mat();
	
	
	
	private Mat featuresUnclustered = new Mat();
	
	
	
//	public static FastFeatureDetector detector = FastFeatureDetector.create();
//	private static BriefDescriptorExtractor extractor = BriefDescriptorExtractor.create();

	@SuppressWarnings({ "resource", "deprecation" })
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
//		BowSift b = new BowSift();
//		SVM train = b.trainBow();

//		 train = train("b",0);

		SVM train = new SVM(new Pointer());
		train = train.loadSVM("d:/imgsvm-model-bow.xml", "");
		
		FileStorage fs = new FileStorage("d:/vocabulary-bow.xml",FileStorage.READ);

		CvMat cm = new CvMat();
		FileNode fn = null;
		Pointer p = null;
		if(fs.isOpened()){
			
			fn = fs.get("vocabulary");
			cm = new CvMat(fn.readObj());
			fs.release();
		}
		vocabulary = new Mat(cm);
		bowDescriptorExtractor.setVocabulary(vocabulary);

		File dir = new File("d:/aaa");
		int total = 0;
		int lj = 0;
		int ljTrue = 0;
		int zc = 0;
		int zcTrue = 0;
		long trainTime = System.currentTimeMillis();
		System.out.println("trainTime:"+TimeUtil.msecToTime(trainTime-startTime));
		for (File file : dir.listFiles()) {
			total++;
			Mat img = imread(file.getPath(),opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
			
			KeyPointVector dstKeyPoints = new KeyPointVector();
			sift.detect(img, dstKeyPoints);
//			Mat features1 = new Mat();
//			sift.compute(mat, dstKeyPoints, features1);
//			clusterData.push_back(features1);
			
			Mat imageDescriptor = new Mat();
			bowDescriptorExtractor.compute(img, dstKeyPoints, imageDescriptor);

//			System.out.println(imageDescriptor.rows() + ", " + imageDescriptor.cols());

//			opencv_imgproc.resize(img, mat, new Size(size, size));
//			opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
//			Mat imgMat = mat.reshape(1, 1);


			Mat sample = new Mat();
			imageDescriptor.convertTo(sample, CV_32FC1);
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
		long endTime = System.currentTimeMillis();
		System.out.println("predictTime:"+TimeUtil.msecToTime(endTime-trainTime));
		// System.out.println("垃圾率:"+(float)lj/total);
//		System.out.println("垃圾图片正确率:" + (ljTrue==0?"无":(float) lj / ljTrue));
//		System.out.println("正常图片正确率:" + (zcTrue==0?"无":(float) zc / zcTrue));
	}
	
	@SuppressWarnings("deprecation")
	private SVM trainBow() {
		Mat trainingData = new Mat();
		List<Integer> trainingLabelsList = new ArrayList<Integer>();
		trainBow("norm");
		trainBow("2dbc");
		trainBow("politics");
		trainBow("ad");
		
//		trainBow("zz");
//		trainBow("qr");
		featuresUnclustered.convertTo(featuresUnclustered, CV_32FC1);
		System.out.println(featuresUnclustered.rows());
		
		int dictionarySize=100;
		//define Term Criteria
		TermCriteria tc = new TermCriteria(CV_TERMCRIT_ITER, 20,0.001);
		//retries number
		int retries=1;
		//necessary flags
		int flags=opencv_core.KMEANS_PP_CENTERS;
		//Create the BoW (or BoF) trainer

		BOWKMeansTrainer bowTrainer = new BOWKMeansTrainer(dictionarySize,tc,retries,flags);
		vocabulary = bowTrainer.cluster(featuresUnclustered);
		bowDescriptorExtractor.setVocabulary(vocabulary);
		
		CvMat cm = new CvMat(vocabulary);
		FileStorage fs = new FileStorage("d:/vocabulary-bow.xml", FileStorage.WRITE);
		if (fs.isOpened()) {
			fs.writeObj("vocabulary", cm);
			fs.release();
		}
		
		trainBow(trainingData,trainingLabelsList,"norm",0);
		trainBow(trainingData,trainingLabelsList,"2dbc",1);
		trainBow(trainingData,trainingLabelsList,"politics",2);
		trainBow(trainingData,trainingLabelsList,"ad",3);
		
//		trainBow(trainingData,trainingLabelsList,"zz",0);
//		trainBow(trainingData,trainingLabelsList,"qr",1);

		
//		train(trainingData, trainingLabelsList, "zz", 1);
//		train(trainingData, trainingLabelsList, "gg", 2);
//		train(trainingData, trainingLabelsList, "qr", 3);
//		train(trainingData, trainingLabelsList, "new", 4);
		SVM svm = trainBow(trainingData, trainingLabelsList);
		svm.save("d:/imgsvm-model-bow.xml");
		return svm;
	}
	
	private void trainBow(String filename) {
		// float[] labels = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
		// List<Integer> list = new ArrayList<Integer>();

		// Mat trainingImages = new Mat();
		try {
			// List<Mat> mobs = new ArrayList<Mat>();

			
			
			
//			Mat clusterData = new Mat();
			
			File dir = new File(filename);
			for (File file : dir.listFiles()) {
				Mat mat = imread(file.getPath(),opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
//				Mat mat = new Mat();
//				opencv_imgproc.resize(img, mat, new Size(size, size));
//				opencv_imgproc.cvtColor(img, mat, opencv_imgproc.COLOR_BGR2GRAY);
//				mat = mat.reshape(1, 1);
				KeyPointVector dstKeyPoints = new KeyPointVector();
				sift.detect(mat, dstKeyPoints);
				Mat features1 = new Mat();
				sift.compute(mat, dstKeyPoints, features1);
//				clusterData.push_back(features1);
				
//				Mat imageDescriptor = new Mat();
//				bowDescriptorExtractor.compute(mat, dstKeyPoints, imageDescriptor);
//				trainingData.push_back(imageDescriptor);
//				trainingLabels.add(label);
				// trainingLabels.push_back();
//				bowTrainer.add(features1);
//				System.out.println(features1.rows() + ", " + features1.cols());
				featuresUnclustered.push_back(features1);
//				System.out.println(featuresUnclustered.rows());
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
	
	private void trainBow(Mat trainingData, List<Integer> trainingLabels, String filename, int label) {
		// float[] labels = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
		// List<Integer> list = new ArrayList<Integer>();

		// Mat trainingImages = new Mat();
		try {
			// List<Mat> mobs = new ArrayList<Mat>();

			
			
			
//			Mat clusterData = new Mat();
			
			File dir = new File(filename);
			for (File file : dir.listFiles()) {
				Mat mat = imread(file.getPath(),opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
//				Mat mat = new Mat();
//				opencv_imgproc.resize(img, mat, new Size(size, size));
//				opencv_imgproc.cvtColor(img, mat, opencv_imgproc.COLOR_BGR2GRAY);
//				mat = mat.reshape(1, 1);
				KeyPointVector dstKeyPoints = new KeyPointVector();
				sift.detect(mat, dstKeyPoints);
//				Mat features1 = new Mat();
//				sift.compute(mat, dstKeyPoints, features1);
//				System.out.println(features1.rows() + ", " + features1.cols());
//				clusterData.push_back(features1);
				
				Mat imageDescriptor = new Mat();
				bowDescriptorExtractor.compute(mat, dstKeyPoints, imageDescriptor);
//				System.out.println(imageDescriptor.rows() + ", " + imageDescriptor.cols());
//				trainingData.push_back(imageDescriptor);
				trainingLabels.add(label);
//				 trainingLabels.push_back();
//				bowTrainer.add(features1);
//				System.out.println(features1.rows() + ", " + features1.cols());
				trainingData.push_back(imageDescriptor);
//				System.out.println(featuresUnclustered.rows());
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
	
	private static SVM trainBow(Mat trainingData, List<Integer> trainingLabelsList) {
		int[] labels = new int[trainingLabelsList.size()];
		for (int i = 0; i < trainingLabelsList.size(); i++) {
			labels[i] = trainingLabelsList.get(i);
		}

		Mat trainingLabels = new Mat(labels);

		trainingData.convertTo(trainingData, CV_32FC1);
		return trainBow(trainingData, trainingLabels);
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
