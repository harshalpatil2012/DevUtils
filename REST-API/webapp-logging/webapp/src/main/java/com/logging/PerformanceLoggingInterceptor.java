package com.logging;

public class PerformanceLoggingInterceptor implements ExchangeFilterFunction {
	private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingInterceptor.class);

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		long startTime = System.currentTimeMillis();

		return next.exchange(request).doOnSuccess(clientResponse -> {
			long executionTime = System.currentTimeMillis() - startTime;
			logger.info("WebClient call to {} took {}ms", request.url(), executionTime);
		}).doOnError(throwable -> {
			long executionTime = System.currentTimeMillis() - startTime;
			logger.error("WebClient call to {} failed after {}ms", request.url(), executionTime, throwable);
		});
	}
}
