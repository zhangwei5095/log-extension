package io.log.extension.agent.core.handler;

import io.log.extension.agent.core.sender.Sender;
import io.log.extension.api.DefaultMessage;

import java.util.Map;

public class DefaultMessageHandler extends AbstractMessageHandler {
	private Sender sender;

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

	@Override
	public void doHandle(DefaultMessage message) {

		// 有错误发送；根消息的状态来记录
		// StorageConcurrentMap storage = StorageConcurrentMap.
		Map<String, Boolean> root = StorageConcurrentMap.getRoot();
		String classNameAndMethodName = message.getRootClassName() + "-"
				+ message.getRootMethodName();

		Boolean hasError = message.getHasError();
		Boolean isRoot = message.getIsRootMessage();

		if (root.containsKey(classNameAndMethodName)) {
			Boolean rootHasError = root.get(classNameAndMethodName);
			if (rootHasError) {
				if (isRoot) { // 根消息
					root.put(classNameAndMethodName, hasError); // 根消息状态加入
				}
				sender.send(message);
			}
		} else {
			if (isRoot) { // 根消息
				root.put(classNameAndMethodName, hasError); // 根消息状态加入
			}
			sender.send(message);
		}
	}

}
