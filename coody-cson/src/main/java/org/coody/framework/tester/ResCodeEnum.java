package org.coody.framework.tester;

/**
 * ��Ϣ��Ӧ��ö��
 * 
 * @author deng
 *
 */
public enum ResCodeEnum {

	SUCCESS(0, "�����ɹ�"),// �ɹ���־
	LOGIN_OUT(1,  "��¼��ʱ"),// ��¼��ʱ
	API_NOT_EXISTS(2,  "����action������"),// ��¼��ʱ
	PARA_ERROR(3,  "������֤��ͨ��"), // ��������
	SYSTEM_ERROR(4,  "ϵͳ��æ�����Ժ�����"), //ϵͳ��æ
	PARA_IS_NULL(5,"����Ϊ��"),//�Զ���α�־
	PARAS_IS_NULL(6,"��������ͬʱΪ��"),//�Զ���α�־
	ACTION_NOT_FOUND(7,"����action������"),
	OTHER(-1,"��������"),
	;
	private int code;
	private String msg;

	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg=msg;
	}
	ResCodeEnum(int code,  String msg) {
		this.code = code;
		this.msg = msg;
	}

}
