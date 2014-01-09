/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.portfolio.web;

import java.security.Principal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.samples.portfolio.Portfolio;
import org.springframework.samples.portfolio.PortfolioPosition;
import org.springframework.samples.portfolio.service.Message;
import org.springframework.samples.portfolio.service.PortfolioService;
import org.springframework.samples.portfolio.service.Trade;
import org.springframework.samples.portfolio.service.TradeService;
import org.springframework.stereotype.Controller;


@Controller
public class PortfolioController {

	private static final Log logger = LogFactory.getLog(PortfolioController.class);

	private final PortfolioService portfolioService;

	private final TradeService tradeService;
    private final MessageSendingOperations<String> messagingTemplate;


    @Autowired
	public PortfolioController(PortfolioService portfolioService, TradeService tradeService, MessageSendingOperations<String> messagingTemplate) {
		this.portfolioService = portfolioService;
		this.tradeService = tradeService;
        this.messagingTemplate = messagingTemplate;
    }

	@SubscribeMapping("/positions")
	public List<PortfolioPosition> getPositions(Principal principal) throws Exception {
		logger.debug("Positions for " + principal.getName());
		Portfolio portfolio = this.portfolioService.findPortfolio(principal.getName());
		return portfolio.getPositions();
	}

    @MessageMapping("/trade")
    public void executeTrade(Trade trade, Principal principal) {
        trade.setUsername(principal.getName());
        logger.debug("Trade: " + trade);
        this.tradeService.executeTrade(trade);
    }

    @MessageMapping("/queue/messages")
    public void sendMessage(Message message, Principal principal) {
        System.out.printf("%s sent %s\n", principal.getName(), message);
        messagingTemplate.convertAndSend("/queue/messages." + message.getRecipient(), message);
    }

    @MessageExceptionHandler
	@SendToUser("/queue/errors")
	public String handleException(Throwable exception) {
		return exception.getMessage();
	}

}
