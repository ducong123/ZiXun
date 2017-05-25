package com.nainaiwang.zixun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.myreceiver.JudgeNetIsConnectedReceiver;
import com.nainaiwang.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



/**
 *
 * @author John
 * @Time 2016-4-13
 *
 */
public class SetDataActivity extends Activity implements OnClickListener {

	private RelativeLayout back,setPortrais,setSex;//返回按钮,设置头像，设置性别
    private LinearLayout setdate;
	private EditText inputUserName,inputIntro,inputSex;//设置用户名，个人介绍
	private TextView dataSave,inputdate;//保存按钮
	private SharedPreferences.Editor edit;
	private ImageView iv_img;

	private JudgeNetIsConnectedReceiver receiver;//广播接受者
	private AlertDialog dialog;//对话框
	private SharedPreferences sp;//存储数据
	private String id;//用户唯一标识
    private boolean isNet;
	private ProgressDialog progressDialog;



	private static final int IMAGE_REQUEST_CODE = 0;// 从相册中选图片的返回码
	private static final int CAMERA_REQUEST_CODE = 1;// 拍照选图片的返回码
	private static final int RESIZE_REQUEST_CODE = 2;// 裁剪返回码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_data);

		initView();// 初始化控件
		initData();
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initData() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences("nainaiwang", MODE_PRIVATE);
		edit = sp.edit();
		id = sp.getString("id", "null");
		/*editDate();*/


		receiver = new JudgeNetIsConnectedReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, intentFilter);
        id = sp.getString("id",null);
		//Intent getIntent = getIntent();
		String nick = sp.getString("nick","null");
		String  birth = sp.getString("birth","null");
		String sign = sp.getString("sign","null");
		String head_pic = sp.getString("head_pic","null");
		//String head = sp.getString("head","null");
		inputUserName.setText(nick);
		inputdate.setText(birth);
		inputIntro.setText(sign);
        x.image().bind(iv_img,head_pic);

		//iv_img.setImageBitmap(head);
        System.out.println("编辑原信息head_pic="+head_pic+"nick="+nick+",sign="+sign+",birth="+birth);
	}
	private void initView() {
		// TODO Auto-generated method stub
	    back = (RelativeLayout)findViewById(R.id.relativelayout_data_back);
		setPortrais = (RelativeLayout)findViewById(R.id.relativelayout_edit_portrais);//调用头像
		inputUserName = (EditText)findViewById(R.id.edit_name) ;//用户名
		inputIntro = (EditText)findViewById(R.id.intro_text);//个人介绍
		/*inputSex =(EditText)findViewById(R.id.edit_sex);//获取性别*/
		inputdate =(TextView)findViewById(R.id.edit_date_time);//获取日期
		dataSave = (TextView)findViewById(R.id.textview_data_save);//保存
		iv_img = (ImageView)findViewById(R.id.imageview_edit_setImg);//图片


		back.setOnClickListener(this);
		setPortrais.setOnClickListener(this);
		inputdate.setOnClickListener(this);//日期弹窗
		dataSave.setOnClickListener(this);//保存按钮
		/*inputSex.setOnClickListener(this);*///性别弹窗
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
	public void onClick(View v) {
		//个人中心
		int id = v.getId();
		switch (id) {
            case R.id.relativelayout_data_back:
                finish();
                break;
            case R.id.relativelayout_edit_portrais://点击设置头像事件
                AlertDialog.Builder builder = new AlertDialog.Builder(this);// 获得builder对象
                LayoutInflater inflater = getLayoutInflater();// 获得布局填充器
                View view = inflater.inflate(R.layout.dialog_edit_activity, null);// 获得自定义布局
                TextView takePhoto = (TextView) view
                        .findViewById(R.id.textview_dialog_edit_takephoto);
                TextView selectPhoto = (TextView) view
                        .findViewById(R.id.textview_dialog_edit_selectphoto);
                takePhoto.setOnClickListener(this);
                selectPhoto.setOnClickListener(this);
                builder.setView(view);// 加上自定义布局
                dialog = builder.create();
                dialog.show();// 显示dialog

                break;
            // 点击拍照上传头像
            case R.id.textview_dialog_edit_takephoto:
                dialog.dismiss();
                takePhoto();// 拍照上传
                break;
            // 点击从相册中选择图片上传头像
            case R.id.textview_dialog_edit_selectphoto:
                dialog.dismiss();
                selectPhotoInAlbum();// 从相册中选取图片上传

                break;
            case R.id.edit_date_time://点击设置日期
				selectDate();
                break;
			case R.id.textview_data_save:
				String nick = inputUserName.getText().toString().trim();
				String sign = inputIntro.getText().toString().trim();
				String brith = inputdate.getText().toString().trim();
               // String head = iv_img.getTag().toString().trim();
                if (TextUtils.isEmpty(nick)) {
					Toast.makeText(SetDataActivity.this, "用户名不能为空",
							Toast.LENGTH_SHORT).show();
				}else if(sign.length()>=30){
					Toast.makeText(SetDataActivity.this, "亲，最多可写30字哦",
							Toast.LENGTH_SHORT).show();
				} else {
					isNet = receiver.judgeNetIsConnected(SetDataActivity.this);
					if(isNet){
						saveDate(nick,brith,sign);
					} else {
						Toast.makeText(SetDataActivity.this, "当前没有网络",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;

			default:
				break;
		}
	}


	/*日期选择*/

public  void selectDate(){
	final java.util.Calendar cd = java.util.Calendar.getInstance();
	DatePickerDialog dialog = new DatePickerDialog(SetDataActivity.this, new DatePickerDialog.OnDateSetListener()
	{

		@RequiresApi(api = Build.VERSION_CODES.N)
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{

			cd.set(year, monthOfYear, dayOfMonth);
			inputdate.setText(DateFormat.format("yyy-MM-dd", cd));
		}
	}, cd.get(java.util.Calendar.YEAR),cd.get(java.util.Calendar.MONTH),cd.get(java.util.Calendar.DAY_OF_MONTH));
	dialog.show();
}


/*日期选择 end*/

    /*	性别选择框 */
	/*private  String[] sexArry = new String[]{"女","男"};
 	private void showSexChooseDialog(){
	AlertDialog.Builder builderSex = new AlertDialog.Builder(this);
		builderSex.setSingleChoiceItems(sexArry,0,new DialogInterface.OnClickListener(){
			@Override
			public  void  onClick(DialogInterface dialog,int which){
				inputSex.setText(sexArry[which]);
				dialog.dismiss();
			}
		});
		builderSex.show();
	}*/
/*	性别选择框 end*/

	//保存个人资料
	private void saveDate(final String str1, final String str2, final String str3){
		System.out.println("token="+ id + ",str1=" +str1+",str2="+str2+",str3"+str3);//打印值

		progressDialog = new ProgressDialog(SetDataActivity.this);
		progressDialog.setMessage("正在加载，请稍候...");
		progressDialog.show();//显示加载动画
		RequestParams saveParams = new RequestParams(UrlUtils.SET_DATA);//调用修改资料接口
		saveParams.addBodyParameter("id",id);
		saveParams.addBodyParameter("nick",str1);
		saveParams.addBodyParameter("birth",str2);
		saveParams.addBodyParameter("sign",str3);
		//saveParams.addBodyParameter("head", str4);

		x.http().post(saveParams, new Callback.CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
			}

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				System.out.println("arg0"+arg0);
				try {
					JSONObject jsonObject1 = new JSONObject(arg0);
					String success = jsonObject1.getString("success");
					String info = jsonObject1.getString("info");
					if ("1".equals(success)) {
						progressDialog.dismiss();
						System.out.println("成功");
						Toast.makeText(SetDataActivity.this, "成功修改",
								Toast.LENGTH_SHORT).show();
						edit.putString("id", id);
						edit.putString("nick", str1);
						edit.putString("birth", str2);
						edit.putString("sign", str3);
						//edit.putString("head", str4);
						edit.commit();
						finish();
					} else if("0".equals(success)){
						Toast.makeText(SetDataActivity.this, info,
								Toast.LENGTH_SHORT).show();
						//progressDialog.dismiss();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}

	/**
	 * 拍照上传
	 */
	private void takePhoto() {
		// TODO Auto-generated method stub
		if (isSdcardExisting()) {
			Intent cameraIntent = new Intent(
					"android.media.action.IMAGE_CAPTURE");// 拍照
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());// 在SD卡上生成的临时的图片，第二个参数为SD卡上临时图片的图片路径
			cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

		} else {
			Toast.makeText(SetDataActivity.this, "请插入sd卡", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 从相册中选取图片上传
	 */
	private void selectPhotoInAlbum() {
		// TODO Auto-generated method stub
		Intent editToAlbum = new Intent(Intent.ACTION_PICK);// 隐式跳转
		//editToAlbum.addCategory(Intent.CATEGORY_OPENABLE);// 添加分类
		editToAlbum.setType("image/*");// 设置类型为图片
		startActivityForResult(editToAlbum, IMAGE_REQUEST_CODE);// 跳转Activity并且返回图片结果码
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {// 如果结果码不等于OK，则返回
			return;
		} else {
			// 判断请求码
			switch (requestCode) {

				case IMAGE_REQUEST_CODE:
					resizeImage(data.getData());// 对从图库返回回来的图片进行裁剪,data.getData()方法获得的是图片的uri，也就是路径
                    //System.out.println("data图"+data.getData());
					break;
				case CAMERA_REQUEST_CODE:
					if (isSdcardExisting()) {
						resizeImage(getImageUri());// 对拍照返回回来的图片进行裁剪,data.getData()方法获得的是图片的uri，也就是路径
					} else {
						Toast.makeText(SetDataActivity.this, "请插入SD卡",
								Toast.LENGTH_LONG).show();
					}
					break;
				// 如果返回的是裁剪码，当data不为空的时候，对data进行操作
				case RESIZE_REQUEST_CODE:
					if (data != null) {
						uploadAndSaveImage(data);// 上传并且保存图片
					}
					break;

				default:
					break;
			}
		}
	}
	/**
	 * 判断SD卡是否存在
	 *
	 * @return
	 */
	private boolean isSdcardExisting() {
		final String state = Environment.getExternalStorageState();// 获取SD卡环境
		// 如果环境正常则返回true，否则返回false
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 裁剪图片
	 *
	 * @param uri
	 *            图片的路径，如果是拍照的话，uri就是拍到的照片的路径，如果是相册的话，uri就是在相册中选取的图片的路径
	 */
	public void resizeImage(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");// 隐式跳转到裁剪功能
		intent.setDataAndType(uri, "image/*");// 设置路径并且设置类型
		intent.putExtra("crop", "true");// 设置图片可以裁剪
		intent.putExtra("aspectX", 1);// 裁剪宽高比为1:1
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);// 保存图片宽高
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESIZE_REQUEST_CODE);// 返回裁剪码
	}

	/**
	 * 上传并且保存图片，先保存到本地，然后获取本地存储头像的路径，然后上传
	 *
	 * @param data
	 *            data是Intent类型的，其中保存着图片信息
	 *
	 */
	private void uploadAndSaveImage(Intent data) {
		System.out.println("data值"+data);
		Bitmap photo = null;// 新建一个Bitmap图片对象
		Bundle extras = data.getExtras();// 取出data中的图片数据

		if (extras != null) {
			photo = extras.getParcelable("data");// 获取到图片
			Bitmap bitmap = data.getParcelableExtra("data");
			this.iv_img.setImageBitmap(bitmap);
			if (isSdcardExisting()) {
				File imageFile = new File(Environment
						.getExternalStorageDirectory().getPath(),
						"setusername.png");// 图片保存路径
				try {
					imageFile.createNewFile();// 创建图片文件
					FileOutputStream fos = new FileOutputStream(imageFile);
					photo.compress(Bitmap.CompressFormat.JPEG, 50, fos);
					fos.flush();
					fos.close();
					String content_type = "multipart/form-data";//内容类型
					System.out.println("图片已经保存到本地了");
					RequestParams uploadParams = new RequestParams(
							UrlUtils.UPLOAD_IMG);//上传头像接口
					//File img = new File(path);
                    uploadParams.setMultipart(true);
					uploadParams.addBodyParameter("token", id);
					uploadParams.addBodyParameter("img",imageFile,content_type);//设置上传图片表单编制为multipart/form-data
					x.http().post(uploadParams, new Callback.CommonCallback<String>() {
						@Override
						public void onCancelled(CancelledException arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onError(Throwable arg0, boolean arg1) {
							// TODO Auto-generated method stub
							/*Toast.makeText(SetDataActivity.this, "头像上传失败，请重新上传",
									Toast.LENGTH_SHORT).show();*/

						}

						@Override
						public void onFinished() {
							// TODO Auto-generated method stub
							//System.out.println("上传完成");
						}

						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub
							System.out.println("修改信息图 = " + arg0);
							try {
								JSONObject jsonObject = new JSONObject(arg0);
								String flag = jsonObject.getString("flag");
							//	String error = jsonObject.getString("error");
								String head_pic =jsonObject.getString("head_pic");
								if ("1".equals(flag)) {
									/*edit.putString("img", img);// 头像保存的本地地址*/
                                    edit.putString("head_pic", head_pic);
									edit.commit();
                                    Toast.makeText(SetDataActivity.this, "上传成功",
                                            Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(SetDataActivity.this,
											"上传失败，请重新上传", Toast.LENGTH_SHORT)
											.show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(SetDataActivity.this, "图片保存失败",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}

			} else {
				Toast.makeText(SetDataActivity.this, "请插入SD卡", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	/**
	 * 获取图片路径
	 */
	private Uri getImageUri() {// 获取路径
		String path = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "setusername.png";// 获取根目录
		File file = new File(path);// 获取根目录
		return Uri.fromFile(file);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

}
