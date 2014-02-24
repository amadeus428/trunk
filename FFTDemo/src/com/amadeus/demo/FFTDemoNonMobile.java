package com.amadeus.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;



public class FFTDemoNonMobile {
	private static final int WAV_FILEHEADER_SIZE = 44;

	private static final int SAMPLESPERSEC = 1024*4;
	private static final int SECONDSIN = 2;

	private static final int BYTESPERSAMPLE = 2;
	public static void main(String[] args) throws IOException {
	
		FileInputStream cin = new FileInputStream("250Hz.wav");
		
		//Move to seek
		for(int i = 0; i<WAV_FILEHEADER_SIZE; i++)
			cin.read();
		
		byte [] input = new byte[SAMPLESPERSEC*BYTESPERSAMPLE]; //2 Bytes per sample
		boolean stereoswitch=true;
		for(int i = 2; i<SAMPLESPERSEC; i+=2){
			if(stereoswitch)
				i-=2;
			cin.read(input, i, 2);
			stereoswitch = !stereoswitch;
		}
		
		ByteBuffer buf = ByteBuffer.wrap(input);
		buf.order(ByteOrder.BIG_ENDIAN);
		
		double[] soundData = new double[SAMPLESPERSEC*2];
//		int runningZeroes=0;
//		int numZeroesBeforeBreak = 10;
//		int i = 0;
		for(int i = 0 ; i < soundData.length/2; i++ ){
//			if(runningZeroes==numZeroesBeforeBreak)
//				break;
			soundData[i]=(double)buf.getShort();
//			if(shortArray[i]==0)
//				runningZeroes++;
		}
//		for(int i = 0; i<SAMPLESPERSEC/4 ;i++){
//			soundData[i]=0;
//			soundData[(soundData.length-1)-i]=0;
//		}
	
		
		DoubleFFT_1D fftDo = new DoubleFFT_1D(SAMPLESPERSEC);
//		double[] fft = new double[input.length * 2];
//		System.arraycopy(input, 0, fft, 0, input.length);
		
		
		fftDo.realForwardFull(soundData);
//		int max = 0;
//		int maxIndex= 0;
//		for(int p = 2; p<intArray.length; p+=2){
//			int current = (int) (intArray[i]*intArray[i] + intArray[i+1]*intArray[i+1]);
//			if(current>max){
//				max = current;
//				maxIndex = p;
//			}
//		}
//		System.out.println(maxIndex*44100/i);
//		System.out.println(maxIndex*22050/i);
		
		double [] magnitude = new double[soundData.length/2];
		for(int i = 0; i< magnitude.length; i++){
			magnitude[i]= soundData[i*2]*soundData[i*2] + soundData[i*2 + 1] * soundData[i*2+1];
		}
		printCSV(magnitude);
		cin.close();
		System.out.println(250*SAMPLESPERSEC/44100);
		System.out.println("Done");
	}
	
	public static short sampleIntsToShort(int a, int b){
		System.out.format("(%X, %X) ", a, b);
		System.out.print(Integer.rotateLeft(b, 8) | a);
		System.out.print("  :   ");
		System.out.println((a << 8) | b);
		
		return (short) ((b << 8) | a);
	}
	public static void printCSV(double[] arr) {
		try {
			BufferedWriter cout = new BufferedWriter(new FileWriter("fft.csv"));
			cout.write(arr[0]+"\n");
			for(double a: arr){
				cout.write(a+"\n");
			}
			cout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}