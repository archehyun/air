package query.checker;

public class ResultMap {
	public String qid;// 질의 아이디
	public boolean result=true;// 결과 정보
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
