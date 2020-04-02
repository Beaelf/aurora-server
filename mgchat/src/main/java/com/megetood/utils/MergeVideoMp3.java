package com.megetood.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

	// ffmpeg.exe所在路径
	private String ffmpegEXE;

	public MergeVideoMp3(String ffmpegEXE) {
		super();
		this.ffmpegEXE = ffmpegEXE;
	}

	/**
	 * 合成音频
	 * @param videoInputPath	输入的视频路径
	 * @param mp3InputPath	bgm路径
	 * @param seconds	截取时长
	 * @param videoOutputPath	输出的视频路径
	 * @throws Exception
	 */
	public void convertor(String videoInputPath, String mp3InputPath,
			double seconds, String videoOutputPath) throws Exception {
//		ffmpeg.exe -i 苏州大裤衩.mp4 -i bgm.mp3 -t 7 -y 新的视频.mp4
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);
		
		command.add("-i");
		command.add(videoInputPath);
		
		command.add("-i");
		command.add(mp3InputPath);
		
		command.add("-t");
		command.add(String.valueOf(seconds));
		
		command.add("-y");
		command.add(videoOutputPath);
		
//		for (String c : command) {
//			System.out.print(c + " ");
//		}
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);
		
		String line = "";
		while ( (line = br.readLine()) != null ) {
		}
		
		if (br != null) {
			br.close();
		}
		if (inputStreamReader != null) {
			inputStreamReader.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}
		
	}

	/**
	 * 合成音频
	 * @param videoInputPath	输入的视频路径
	 * @param mp3InputPath	bgm路径
	 * @param seconds	截取时长
	 * @param videoOutputPath	输出的视频路径
	 * @throws Exception
	 */
	public void convertor(String videoInputPath, String mp3InputPath,
						  String videoOutputPath) throws Exception {
//		ffmpeg.exe -i 苏州大裤衩.mp4 -i bgm.mp3 -t 7 -y 新的视频.mp4
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);

		command.add("-i");
		command.add(videoInputPath);

		command.add("-i");
		command.add(mp3InputPath);

		command.add("-y");
		command.add(videoOutputPath);

//		for (String c : command) {
//			System.out.print(c + " ");
//		}

		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();

		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);

		String line = "";
		while ( (line = br.readLine()) != null ) {
		}

		if (br != null) {
			br.close();
		}
		if (inputStreamReader != null) {
			inputStreamReader.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}

	}

//	public static void main(String[] args) {E:\soft\ffmpeg\bin
//		MergeVideoMp3 ffmpeg = new MergeVideoMp3("C:\\ffmpeg\\bin\\ffmpeg.exe");
//		try {
//			ffmpeg.convertor("C:\\苏州大裤衩.mp4", "C:\\music.mp3", 7.1, "C:\\这是通过java生产的视频.mp4");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
