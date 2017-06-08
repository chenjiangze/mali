package mali.core.util;

/**
 * jsoup文档在解析之前所做的工作
 */
public interface IDocumentPrepared {
	String prepare(String body);
}
