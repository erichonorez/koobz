package org.svomz.apps.koobz.application;

import java.text.MessageFormat;

public class WorkflowNotFoundException extends Exception {

  private static final String MESSAGE_TEMPLATE = "Board with id {0} not found";

  public WorkflowNotFoundException(final String aBoardId) {
    super(MessageFormat.format(MESSAGE_TEMPLATE, aBoardId));
  }
}
