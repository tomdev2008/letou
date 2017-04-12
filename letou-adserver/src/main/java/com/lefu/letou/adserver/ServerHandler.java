package com.lefu.letou.adserver;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.lefu.letou.adserver.model.AdException;
import com.lefu.letou.adserver.service.AppService;
import com.lefu.letou.adserver.service.PosAdService;
import com.lefu.letou.common.Params;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	private HttpRequest request;

	private static Logger logger;
	private static Logger errorLog;

	private boolean isDebug = false;
	private boolean isApp = false;

	private Map<String, String> params = new HashMap<String, String>();
	private static PosAdService posService = new PosAdService();
	private static AppService appService = new AppService();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;
			parseParam();
			init();
			String uri = request.getUri();
			if (uri.startsWith("/s")) {
				posService.recordPosAd(params);
				writeResult(ctx, "ok");
			} else {
				logger.info("requestURI:" + uri);
				checkParam();
				String json;
				if (isApp) {
					json = appService.findAd(params, isDebug);
				} else {
					json = posService.findAd(params, isDebug);
				}
				writeResult(ctx, json);
			}
		}
	}

	private void init() {
		isDebug = "1".equals(params.get(Params.TEST));
		String app = params.get(Params.APP);
		isApp = "1".equals(app);
		if (isApp) {
			logger = Logger.getLogger("applog");
			errorLog = Logger.getLogger("appErrorLog");
		} else {
			logger = Logger.getLogger("poslog");
			errorLog = Logger.getLogger("posErrorLog");
		}
	}

	private void parseParam() {
		QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
		Map<String, List<String>> parameters = decoder.parameters();
		Iterator<Entry<String, List<String>>> iterator = parameters.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, List<String>> next = iterator.next();
			List<String> value = next.getValue();
			if (value != null) {
				params.put(next.getKey(), next.getValue().get(0));
			}
		}
	}

	private void writeResult(ChannelHandlerContext ctx, String json)
			throws UnsupportedEncodingException {
		FullHttpResponse response;
		if (json == null) {
			response = new DefaultFullHttpResponse(HTTP_1_1,
					HttpResponseStatus.NO_CONTENT,
					PooledByteBufAllocator.DEFAULT.directBuffer().writeBytes(
							"".getBytes("utf-8")));
		} else {
			// response = new DefaultFullHttpResponse(HTTP_1_1, OK,
			// Unpooled.wrappedBuffer(json.getBytes("UTF-8")));

			response = new DefaultFullHttpResponse(HTTP_1_1, OK,
					PooledByteBufAllocator.DEFAULT.directBuffer().writeBytes(
							json.getBytes("utf-8")));
		}
		response.headers().set(CONTENT_TYPE, "application/json; charset=utf-8");
		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());
		if (HttpHeaders.isKeepAlive(request)) {
			response.headers().set(CONNECTION, Values.KEEP_ALIVE);
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private void checkNull(String key) throws AdException {
		if (params.get(key) == null) {
			throw new AdException(key + " is null");
		}
	}

	private void checkParam() throws AdException {
		String bid = params.get(Params.BUSINESS_ID); // 商户id
		if (!StringUtils.isNumeric(bid)) {
			throw new AdException("bid is wrong", bid);
		}
		String pid = params.get(Params.PRODUCT_ID);
		if (!StringUtils.isNumeric(pid)) {
			throw new AdException("pid is wrong", pid);
		}
		if (!isApp) {
			// 不是app,必须有终端号
			checkNull(Params.DEVICE_ID);
			checkNull(Params.BUSINESS_ID); // 商户id
			checkNull(Params.TIME);
		} else {
			checkNull(Params.IP);
			checkIP(params.get(Params.IP));
			checkNull(Params.USER_AGENT);
			checkNull(Params.TIME);
		}
	}

	private void checkIP(String ip) throws AdException {
		if (ip == null || ip.equals("")) {
			throw new AdException("ip is wrong");
		}
		String[] split = ip.split("\\.");
		if (split.length != 4) {
			throw new AdException("ip is wrong");
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		String msg = "Internal Server Error";
		HttpResponseStatus stat = HttpResponseStatus.INTERNAL_SERVER_ERROR;
		if (cause instanceof AdException) {
			AdException e = (AdException) cause;
			msg = cause.getMessage();
			stat = HttpResponseStatus.BAD_REQUEST;
			msg += ">>" + e.getParam() + "<<";
		}
		StackTraceElement stackTraceElement = cause.getStackTrace()[0];
		String fileName = stackTraceElement.getFileName();
		int lineNumber = stackTraceElement.getLineNumber();
		if (cause.getCause() != null) {
			errorLog.error(fileName + lineNumber + "," + cause.getMessage()
					+ "," + cause.getCause().getMessage(), cause);
		} else {
			errorLog.error(fileName + lineNumber + "," + cause.getMessage(), cause);
		}
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, stat,
				Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
