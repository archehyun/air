package query.checker;

public class ResultMap {
	public String qid;// ���� ���̵�
	public boolean result=true;// ��� ����
	public String getQid() {
		return qid;
	}
	public boolean isResult() {
		return result;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
}
