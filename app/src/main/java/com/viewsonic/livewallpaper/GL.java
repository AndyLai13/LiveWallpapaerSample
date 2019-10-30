package com.viewsonic.livewallpaper;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class GL {
	static class Triangle {

		private final String vertexShaderCode =
				// This matrix member variable provides a hook to manipulate
				// the coordinates of the objects that use this vertex shader
				"uniform mat4 uMVPMatrix;" +
						"attribute vec4 vPosition;" +
						"void main() {" +
						// the matrix must be included as a modifier of gl_Position
						// Note that the uMVPMatrix factor *must be first* in order
						// for the matrix multiplication product to be correct.
						"  gl_Position = uMVPMatrix * vPosition;" +
						"}";

		private final String fragmentShaderCode =
				"precision mediump float;" +
						"uniform vec4 vColor;" +
						"void main() {" +
						"  gl_FragColor = vColor;" +
						"}";

		// Use to access and set the view transformation
		private int mMVPMatrixHandle;

		private FloatBuffer vertexBuffer;

		// number of coordinates per vertex in this array
		static final int COORDS_PER_VERTEX = 3;
		static float triangleCoords[] = {   // in counterclockwise order:
				0.0f, 0.433f, 0.0f, // top
				-0.5f, -0.433f, 0.0f, // bottom left
				0.5f, -0.433f, 0.0f
		};

		// Set color with red, green, blue and alpha (opacity) values
		float color[] = { 255, 0, 1.0f, 1.0f };

		private final int mProgram;

		public Triangle() {
			// 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
			ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
			// 数组排列用nativeOrder
			bb.order(ByteOrder.nativeOrder());
			// 从ByteBuffer创建一个浮点缓冲区
			vertexBuffer = bb.asFloatBuffer();
			// 将坐标添加到FloatBuffer
			vertexBuffer.put(triangleCoords);
			// 设置缓冲区来读取第一个坐标
			vertexBuffer.position(0);

			int vertexShader = VideoWallPaperService.loadShader(GLES20.GL_VERTEX_SHADER,
					vertexShaderCode);
			int fragmentShader = VideoWallPaperService.loadShader(GLES20.GL_FRAGMENT_SHADER,
					fragmentShaderCode);

			// 创建空的OpenGL ES程序
			mProgram = GLES20.glCreateProgram();

			// 添加顶点着色器到程序中
			GLES20.glAttachShader(mProgram, vertexShader);

			// 添加片段着色器到程序中
			GLES20.glAttachShader(mProgram, fragmentShader);

			// 创建OpenGL ES程序可执行文件
			GLES20.glLinkProgram(mProgram);
		}

		private int mPositionHandle;
		private int mColorHandle;

		private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
		private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

		public void draw(float[] mvpMatrix) {
			// 得到形状的变换矩阵的句柄
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

			// 将投影和视图转换传递给着色器
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

			// 画三角形
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

			// 禁用顶点数组
			GLES20.glDisableVertexAttribArray(mPositionHandle);

			// 将程序添加到OpenGL ES环境
			GLES20.glUseProgram(mProgram);

			// 获取顶点着色器的位置的句柄
			mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

			// 启用三角形顶点位置的句柄
			GLES20.glEnableVertexAttribArray(mPositionHandle);

			//准备三角形坐标数据
			GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
					GLES20.GL_FLOAT, false,
					vertexStride, vertexBuffer);

			// 获取片段着色器的颜色的句柄
			mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

			// 设置绘制三角形的颜色
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);

			// 绘制三角形
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

			// 禁用顶点数组
			GLES20.glDisableVertexAttribArray(mPositionHandle);
		}

	}

	static class Square {

		private FloatBuffer vertexBuffer;
		private ShortBuffer drawListBuffer;

		// number of coordinates per vertex in this array
		static final int COORDS_PER_VERTEX = 3;
		static float squareCoords[] = {
				-0.5f,  0.5f, 0.0f,   // top left
				-0.5f, -0.5f, 0.0f,   // bottom left
				0.5f, -0.5f, 0.0f,   // bottom right
				0.5f,  0.5f, 0.0f }; // top right

		private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

		public Square() {
			// 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
			ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
			bb.order(ByteOrder.nativeOrder());
			vertexBuffer = bb.asFloatBuffer();
			vertexBuffer.put(squareCoords);
			vertexBuffer.position(0);

			// 初始化ByteBuffer，长度为arr数组的长度*2，因为一个short占2个字节
			ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
			dlb.order(ByteOrder.nativeOrder());
			drawListBuffer = dlb.asShortBuffer();
			drawListBuffer.put(drawOrder);
			drawListBuffer.position(0);
		}
	}

	private IntBuffer intBufferUtil(int[] arr) {
		IntBuffer mBuffer;
		// 初始化ByteBuffer，长度为arr数组的长度*4，因为一个int占4个字节
		ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
		// 数组排列用nativeOrder
		qbb.order(ByteOrder.nativeOrder());
		mBuffer = qbb.asIntBuffer();
		mBuffer.put(arr);
		mBuffer.position(0);
		return mBuffer;
	}

	private FloatBuffer floatBufferUtil(float[] arr) {
		FloatBuffer mBuffer;
		// 初始化ByteBuffer，长度为arr数组的长度*4，因为一个int占4个字节
		ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
		// 数组排列用nativeOrder
		qbb.order(ByteOrder.nativeOrder());
		mBuffer = qbb.asFloatBuffer();
		mBuffer.put(arr);
		mBuffer.position(0);
		return mBuffer;
	}

	private ShortBuffer shortBufferUtil(short[] arr) {
		ShortBuffer mBuffer;
		// 初始化ByteBuffer，长度为arr数组的长度*2，因为一个short占2个字节
		ByteBuffer dlb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 2 bytes per short)
				arr.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		mBuffer = dlb.asShortBuffer();
		mBuffer.put(arr);
		mBuffer.position(0);
		return mBuffer;
	}
}
