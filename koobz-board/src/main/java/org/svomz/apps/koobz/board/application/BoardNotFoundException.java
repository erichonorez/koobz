package org.svomz.apps.koobz.board.application;

import java.text.MessageFormat;

public class BoardNotFoundException extends Exception {

  private static final String MESSAGE_TEMPLATE = "Board with id {0} not found";

  public BoardNotFoundException(final String aBoardId) {
    super(MessageFormat.format(MESSAGE_TEMPLATE, aBoardId));
  }
}
