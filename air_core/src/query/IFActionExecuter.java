package query;

import java.util.List;

import msg.node.InboundMsgForData;
import msg.node.MsgForAPI;
import buffer.info.ActionInfo;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public interface IFActionExecuter {


	public String IF_QueryAction(List<ActionInfo> actionList, int[][] queryResult,InboundMsgForData data,String userID);

	public MsgForAPI createMsg(String string, int i, String acitonResult);

	public void execute(MsgForAPI msg);

}
