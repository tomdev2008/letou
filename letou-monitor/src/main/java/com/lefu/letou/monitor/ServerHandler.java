package com.lefu.letou.monitor;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
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
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.lefu.letou.common.Params;
import com.lefu.letou.monitor.util.LoggerUtil;
import com.lefu.letou.monitor.util.MonitorException;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	private HttpRequest request;
	private Map<String, String> params = new HashMap<String, String>();
	private Logger logger;
	private boolean isClickRequest; // 默认曝光请求

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;
			parseParam();
			init();

			if (isClickRequest) {
				String ldp = params.get(Params.O);
				redirect(ctx, ldp);
			} else {
				writeResult(ctx, "{\"success\":true}");
			}
			logger.info("requestURI:" + request.getUri());
		}
	}

	private void redirect(ChannelHandlerContext ctx, String ldp) {
		HttpResponse response;
		response = new DefaultFullHttpResponse(HTTP_1_1,
				HttpResponseStatus.FOUND);
		response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
		if (ldp != null) {
			response.headers().set(LOCATION, ldp);
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private void init() {
		if (isClickRequest) {
			logger = LoggerUtil.clickLogger;
		} else {
			logger = LoggerUtil.impressionLogger;
		}
	}

	private void parseParam() {
		String uri = request.getUri();
		if (uri.startsWith("/c")) {
			isClickRequest = true;
		}

		QueryStringDecoder decoder = new QueryStringDecoder(uri);
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable ex)
			throws Exception {
		String msg = "Internal Server Error";
		HttpResponseStatus stat = HttpResponseStatus.INTERNAL_SERVER_ERROR;
		if (ex instanceof MonitorException) {
			MonitorException e = (MonitorException) ex;
			msg = e.getMessage();
			stat = HttpResponseStatus.BAD_REQUEST;
			msg += ">>" + e.getParam() + "<<";
		}
		if (ex.getCause() != null) {
			LoggerUtil.getLogger().error(
					ex.getMessage() + "," + ex.getCause().getMessage());
		} else {
			LoggerUtil.getLogger().error(ex.getMessage());
		}
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, stat,
				Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

}
