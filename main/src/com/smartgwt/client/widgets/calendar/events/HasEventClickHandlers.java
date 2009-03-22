/*
 * SmartGWT (GWT for SmartClient)
 * Copyright 2008 and beyond, Isomorphic Software, Inc.
 *
 * SmartGWT is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.  SmartGWT is also
 * available under typical commercial license terms - see
 * http://smartclient.com/license
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
 
package com.smartgwt.client.widgets.calendar.events;

import com.smartgwt.client.event.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasEventClickHandlers extends HasHandlers {
    /**
     * Called whenever an event is clicked on in the day, week or month views. <P> By default a dialog appears showing details for the event, and offering the ability to edit events which are editable.  Return false to cancel the default action. This is a good place to, for example, show a completely customized event dialog instead of the default one.
     */
    HandlerRegistration addEventClickHandler(EventClickHandler handler);
}
