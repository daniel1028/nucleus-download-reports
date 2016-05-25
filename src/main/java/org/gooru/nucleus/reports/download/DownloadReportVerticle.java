package org.gooru.nucleus.reports.download;

import java.io.File;

import org.gooru.nucleus.reports.downlod.service.ClassExportService;
import org.gooru.nucleus.reports.infra.constants.MessagebusEndpoints;
import org.gooru.nucleus.reports.infra.util.MessageResponse;
import org.gooru.nucleus.reports.infra.util.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish
 */
public class DownloadReportVerticle extends AbstractVerticle {
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadReportVerticle.class);

	private static final ClassExportService classExportService = ClassExportService.instance();
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		EventBus eb = vertx.eventBus();
		MessageConsumer<Object> status = null;

		status = eb.localConsumer(MessagebusEndpoints.MBEP_DOWNLOAD_REQUEST, message -> {

			LOGGER.debug("Received message: '{}'", message.body());
			vertx.executeBlocking(future -> {
				// FIXME: Dummy implementation
				File result =  classExportService.exportCsv("2ac95029-7f46-48e9-9f9d-1989f6610b8b", "1fe534e6-aa6a-4487-aaed-a055f46c4cb1", "20b89e9b-f673-4c5f-bfab-3f377a663be8", null, null, "unit", null);
				future.complete(result);
			}, res -> {
				MessageResponse result = (MessageResponse) res.result();
				LOGGER.debug("Sending response: '{}'", result.reply());
				message.reply(result.reply(), result.deliveryOptions());

			});

		});

		status = eb.localConsumer(MessagebusEndpoints.MBEP_DOWNLOAD_REQUEST_STATUS, message -> {

			LOGGER.debug("Received message: '{}'", message.body());
			vertx.executeBlocking(future -> {
				// FIXME: Dummy implementation
				//MessageResponse result = MessageResponseFactory.createOkayResponse(new JsonObject("" + message.body()));
				File result =  classExportService.exportCsv("2ac95029-7f46-48e9-9f9d-1989f6610b8b", "1fe534e6-aa6a-4487-aaed-a055f46c4cb1", "20b89e9b-f673-4c5f-bfab-3f377a663be8", null, null, "unit", null);
				future.complete(result);
			}, res -> {
				MessageResponse result = (MessageResponse) res.result();
				LOGGER.debug("Sending response: '{}'", result.reply());
				message.reply(result.reply(), result.deliveryOptions());

			});

		});

		status.completionHandler(result -> {
			if (result.succeeded()) {
				LOGGER.info("User end point ready to listen");
				startFuture.complete();
			} else {
				LOGGER.error("Error registering the User handler on message bus");
				startFuture.fail(result.cause());
			}
		});

	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		// Currently a noop
		stopFuture.complete();
	}

}
