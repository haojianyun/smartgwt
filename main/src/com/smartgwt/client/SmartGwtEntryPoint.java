/*
 * Smart GWT (GWT for SmartClient)
 * Copyright 2008 and beyond, Isomorphic Software, Inc.
 *
 * Smart GWT is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * is published by the Free Software Foundation.  Smart GWT is also
 * available under typical commercial license terms - see
 * http://smartclient.com/license
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package com.smartgwt.client;

import java.util.Set;

import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.util.I18nUtil;
import com.smartgwt.client.util.LogUtil;
import com.smartgwt.client.bean.BeanFactory;
import com.smartgwt.client.util.SC;

/**
 * Internal Smart GWT Entry point class where framework level initialization code executes
 * before the users EntryPoint is run.
 */
public class SmartGwtEntryPoint implements EntryPoint {
    private static boolean initialized = false;

    private static native void init() /*-{
        
        // If we can't find window.isc, the JavaScript libs are not present.
        if ($wnd.isc == null) {
            var message = "Core SmartClient JavaScript libraries appear not to be loaded.\nIf inheriting the NoScript SmartGWT modules, verify that " +
                            "the HTML file includes <script src=...> tags to load the SmartClient module .js files from the appropriate location within the " +
                            "WAR.\nBy default these files are present under [GWT app name]/sc/modules/. ";
            @com.google.gwt.core.client.GWT::log(Ljava/lang/String;Ljava/lang/Throwable;)(message, @com.smartgwt.client.core.JsObject.SGWT_WARN::new(Ljava/lang/String;)(message));
            return;

        }

        var asBuiltSCVersionNumber = @com.smartgwt.client.Version::getSCVersionNumber()();
        if ($wnd.isc.versionNumber != asBuiltSCVersionNumber && $wnd.isc.versionNumber.indexOf("${")==-1 ) {
            var message = "This build of Smart GWT " + @com.smartgwt.client.Version::getVersion()() + " was built for SmartClient version " + asBuiltSCVersionNumber +
                          " but SmartClient version " + $wnd.isc.versionNumber + " is loaded.\n\nTo correct this problem, clear GWT's unitcache, run a GWT compile, " +
                          "restart the browser, and clear the browser's cache before visiting any pages.";
            @com.google.gwt.core.client.GWT::log(Ljava/lang/String;Ljava/lang/Throwable;)(message, @com.smartgwt.client.core.JsObject.SGWT_WARN::new(Ljava/lang/String;)(message));
        }

        // these must be called after we verify the SC libs are loaded
        @com.smartgwt.client.util.LogUtil::setJSNIErrorHandler()();
        @com.smartgwt.client.util.LogUtil::addSGWTLoggerCategories()();

        //pre GWT 2.0 fallback
        if(typeof $entry === "undefined") {
            $entry = function(jsFunction) {
                        return jsFunction;
                     };
        }

        if ($wnd.isc.Browser.isIE && $wnd.isc.Browser.version >= 7) {
            $wnd.isc.EventHandler._IECanSetKeyCode = {};
        }
        // Debox Javascript objects that wrap primitives for the benefit of Java in hosted mode.
        // E.g. JS Boolean or Number must be converted to primitives to return them from JSNI.
        // Note: Java boxing (e.g. java.lang.Boolean) is separate & requires adding extra code.
        $debox = function(val) {
            return @com.google.gwt.core.client.GWT::isScript()() ? val : function() {
            var v = val.apply(this, arguments);
            // Dates can just be returned without deboxing
            if ($wnd.isc.isA.Date(v)) return v;
            return v == undefined || v == null ? null : v.valueOf();
        }};
        
        // use a new Record as the array loading marker (this will allow new Record(...) to work with unloaded rows)
        var loadingRecord = @com.smartgwt.client.data.Record::new()();
        $wnd.Array.LOADING = loadingRecord.@com.smartgwt.client.data.Record::getJsObj()();
        $wnd.Array.LOADING.loadingMarker = true;
        
        // Set a flag so SC code can easily determine that SGWT is running
        $wnd.isc.Browser.isSGWT = true;

        //convert javascript data types into corresponding Java wrapper types
        //int -> Integer, float -> Float, boolean -> Boolean and date - > java.util.Date
        $wnd.SmartGWT ={};
        
        // In JSNI, we may be passed GWT Java Object references.
        // These typically can not be directly manipulated -- this check will test for such objects.
        $wnd.SmartGWT.isNativeJavaObject = function (object) {
            // From observation "typeof" reports "function" for native Java objects in Firefox in development mode
            // and in OmniWeb, in development mode
            // In all other browsers, typeof reports as "object"
            var type = typeof object;
            if (type != "function" && type != "object") return false;
            if (@com.smartgwt.client.util.JSOHelper::isJSO(Ljava/lang/Object;)(object)) return false;
            return true;
        };

        // provide a way to check whether an SC.REF is really a SGWT Tab or some other widget 
        $wnd.SmartGWT.isTab = function (target) {
            return @com.smartgwt.client.widgets.tab.Tab::isTab(Ljava/lang/Object;)(target);
        }

        // Helper JSNI method to throw an exception if attempting to convert an unconvertible native Java object to JavaScript
        // This may be called from SmartClient framework code. Useful for the case where a developer has applied a native Java
        // object to a DS transaction's data via 'setAttribute' or similar, and we want to throw a clear exception when
        // attempting to serialize this data and send to the server.
        $wnd.SmartGWT.throwUnconvertibleObjectException = function (object, message) {
        	@com.smartgwt.client.util.JSOHelper::throwUnconvertibleObjectException(Ljava/lang/Object;Ljava/lang/String;)(object,message);
        }

        if(!@com.google.gwt.core.client.GWT::isScript()()){
            $wnd.isc.Log.addClassMethods({
              warningLogged : function (message) {
                  @com.google.gwt.core.client.GWT::log(Ljava/lang/String;Ljava/lang/Throwable;)(message, @com.smartgwt.client.core.JsObject.SGWT_WARN::new(Ljava/lang/String;)(message));
              }
            });

            //support option of triggering JS debugger by default in hosted mode if JS error is encountered
            @com.smartgwt.client.util.SC::setEnableJSDebugger(Z)(true);
            
            // Log a warning about the known issues with Chrome / Hosted Mode
            if ($wnd.isc.Browser.isChrome) {
                $wnd.isc.Log.logWarn("WARNING: due to bugs in Chrome, GWT development mode in Chrome is not reliable and should not be used.  " +
                    "This does not affect compiled mode in Chrome, which works.  Note that the same bug makes GWT development " +
                    "mode in Chrome very slow as well, so other browsers will be faster as " +
                    "well.  More details including links to Chrome bugs here: " +
                    "http://forums.smartclient.com/showthread.php?t=8159#aChrome");
            }

            $wnd.isc.isA.FUNCTION_STR = '[object Function]';
            $wnd.isc.isA.DATE_STR = '[object Date]';
            $wnd.isc.isA.ARRAY_STR = '[object Array]';
            
            $wnd.isc.isA.Function = function (object) {
                if (object == null) return false;
                if ($wnd.SmartGWT.isNativeJavaObject(object)) return false;
                return Object.prototype.toString.apply(object) === this.FUNCTION_STR;
            };
            $wnd.isc.isA.String = function (object) {
                if (object == null) return false;
                if ($wnd.SmartGWT.isNativeJavaObject(object)) return false;
                if (object.Class != null && object.Class == 'String') return true;
                return typeof object == "string";
            };
            $wnd.isc.isA.Number = function (object) {
                if (object == null) return false;
                if ($wnd.SmartGWT.isNativeJavaObject(object)) return false;
                if (object.Class != null && object.Class == 'Number') return true;
                return typeof object === 'number' && isFinite(object);
            };
            $wnd.isc.isA.Boolean = function (object) {
                if (object == null) return false;
                if ($wnd.SmartGWT.isNativeJavaObject(object)) return false;
                return typeof object == "boolean";
            };
            $wnd.isc.isA.Date = function (object) {
                if (object == null) return false;
                if ($wnd.SmartGWT.isNativeJavaObject(object)) return false;
                return Object.prototype.toString.apply(object) === this.DATE_STR;
            };
            $wnd.isc.isA.Array = function (object) {
                if (object == null) return false;
                if ($wnd.SmartGWT.isNativeJavaObject(object)) return false;
                return Object.prototype.toString.apply(object) === this.ARRAY_STR;
            };

            $wnd.isc.Canvas.validateFieldNames = true;
        }

        // helper routine for convertToJavaType(); not wrapped with $entry()
        $wnd.SmartGWT._convertToJavaArrayType = function (obj, type) {
            if ($wnd.isc.SimpleType.inheritsFrom(type, "text")) {
                return @com.smartgwt.client.util.JSOHelper::convertToJavaStringArray(Lcom/google/gwt/core/client/JavaScriptObject;)(obj);
            } else if ($wnd.isc.SimpleType.inheritsFrom(type, "date")) {
                return @com.smartgwt.client.util.JSOHelper::convertToJavaDateArray(Lcom/google/gwt/core/client/JavaScriptObject;)(obj);
            } else if ($wnd.isc.SimpleType.inheritsFrom(type, "boolean")) {
                return @com.smartgwt.client.util.JSOHelper::convertToJavaBooleanArray(Lcom/google/gwt/core/client/JavaScriptObject;)(obj);
            } else if ($wnd.isc.SimpleType.inheritsFrom(type, "integer")) {
                return @com.smartgwt.client.util.JSOHelper::convertToJavaIntegerArray(Lcom/google/gwt/core/client/JavaScriptObject;)(obj);
            } else if ($wnd.isc.SimpleType.inheritsFrom(type, "float")) {
                return @com.smartgwt.client.util.JSOHelper::convertToJavaDoubleArray(Lcom/google/gwt/core/client/JavaScriptObject;)(obj);
            } else {
                return @com.smartgwt.client.util.JSOHelper::convertToJavaObjectArray(Lcom/google/gwt/core/client/JavaScriptObject;)(obj);
            }
        };

        //> @method convertToJavaType()
        // Converts a JS object to a Java object.  The type argument is optional
        // and only used when isc.isAn.Array(obj) is true.  The type is determined
        // by inspecting the object in all other cases.
        // Note: If null is explicitly passed for type, conversion to a Java array
        // (if applicable) will be skipped and a JavaScriptObject will be returned.
        // @param obj (object) the JS object to be converted
        // @param [type] (String) type of the field as in +link{DataSourceField.type} (optional)
        //<
        $wnd.SmartGWT.convertToJavaType = $entry(function(obj, type) {
        		if(obj == null) return null;
                
                var objType = typeof obj;

                if(objType == 'string') {
                    return obj;
                } else if (objType == 'number') {
                    if(obj.toString().indexOf('.') == -1) {
                        if(obj <= @java.lang.Integer::MAX_VALUE && obj >= @java.lang.Integer::MIN_VALUE) {
                            return @com.smartgwt.client.util.JSOHelper::toInteger(I)(obj);
                        } else {
                          return @com.smartgwt.client.util.JSOHelper::toLong(D)(obj);
                        }
                    } else {
                        // Convert non-integral JS numbers to Java `Double's to prevent a loss
                        // of precision in dev mode and other issues where certain numbers can
                        // be printed with extra spurious precision.
                        // See the comment in JSOHelper.doubleValue().
                        return @com.smartgwt.client.util.JSOHelper::toDouble(D)(obj);
                    }
                } else if(objType == 'boolean') {
                    return @com.smartgwt.client.util.JSOHelper::toBoolean(Z)(obj);
                    
                // We may already be looking at a native java object. If so, just return it
                // (Note that attempting to look at properties such as _constructor will crash on a native java object)
                } else if ($wnd.SmartGWT.isNativeJavaObject(obj)) {
                	return obj;

                } else if($wnd.isc.isA.Date(obj)) {
                    return @com.smartgwt.client.util.JSOHelper::convertToJavaDate(Lcom/google/gwt/core/client/JavaScriptObject;)(obj);
                } else if (obj._constructor && obj._constructor == 'DateRange') {
                    return @com.smartgwt.client.widgets.form.fields.DateRangeItem::convertToDateRange(Lcom/google/gwt/core/client/JavaScriptObject;)(obj);
                } else if($wnd.isc.isA.Array(obj) && type !== null) {
                    return this._convertToJavaArrayType(obj, type);
                } else if(@com.smartgwt.client.util.JSOHelper::isJSO(Ljava/lang/Object;)(obj)) {
                    return obj;
                } else {
                	// We were unable to determine the type - return the object unmodified.
                    return obj;
                }
        });

        $wnd.SmartGWT.convertToJavaObject = $entry(function (object, listAsArray, forceMap) {
        	
            if (object == null) return null;
            var refProperty = @com.smartgwt.client.util.SC::REF;

	    	if (!$wnd.isc.isA.Object(object)) {
        	
	    		return $wnd.SmartGWT.convertToJavaType(object);
	    	} else if ($wnd.isc.isA.Date(object)) {

                return @com.smartgwt.client.util.JSOHelper::convertToJavaDate(Lcom/google/gwt/core/client/JavaScriptObject;)(object);
	    	} else if ($wnd.isc.isAn.Array(object)) {

	    		var convertedArray = [];
	    		for (var i = 0; i < object.length; i++) {
	    			convertedArray[i] =  $wnd.SmartGWT.convertToJavaObject(object[i], false, false);
	    		}
	    		// now we've converted all our members and we need to return a Java array or List
	    		if (listAsArray) {
	    			return  @com.smartgwt.client.util.JSOHelper::convertToJavaObjectArray(Lcom/google/gwt/core/client/JavaScriptObject;)(convertedArray);
	    		} else {

	    			var javaList = @java.util.ArrayList::new()();
	    			for (var i = 0; i < convertedArray.length; i++) {
	    				javaList.@java.util.ArrayList::add(Ljava/lang/Object;)(convertedArray[i]);
	    			}
	    			return javaList;
	    		}
            } else {
                if (forceMap !== true) {
                    // Check for a POJO.
                    if (!@com.smartgwt.client.util.JSOHelper::isJSO(Ljava/lang/Object;)(object)) {
                        return object;
                    }

                    if (object[refProperty] != null) {
                        return object[refProperty];
                    }
                    if ($wnd.isc.isA.Canvas(object)) {
                        return @com.smartgwt.client.widgets.Canvas::getById(Ljava/lang/String;)(object.getID());
                    }

                    if ($wnd.isc.isA.String(object.name) && $wnd.isc.isA.DynamicForm(object.form)) {
                        var formJ = @com.smartgwt.client.widgets.form.DynamicForm::getOrCreateRef(Lcom/google/gwt/core/client/JavaScriptObject;)(object.form);
                        return formJ.@com.smartgwt.client.widgets.form.DynamicForm::getField(Ljava/lang/String;)(object.name);
                    }

                    if ($wnd.isc.isA.String(object._constructor)) {
                        var objectConstructor = object._constructor;
                        if (objectConstructor == "RelativeDate") {
                            return (object[refProperty] = @com.smartgwt.client.data.RelativeDate::new(Lcom/google/gwt/core/client/JavaScriptObject;)(object));
                        }
                        // Don't convert `AdvancedCriteria' here; we want this API to return
                        // a `Map' for the advanced criteria JSO.
                    }

                    if ($wnd.isc.isAn.Instance(object) && object.getClassName != null) {
                        return (object[refProperty] = @com.smartgwt.client.util.ObjectFactory::createInstance(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(object.getClassName(), object));
                    }
                } else {
                    if (object[refProperty] != null) {
                        if (@com.smartgwt.client.util.JSOHelper::isJavaMap(Ljava/lang/Object;)(object[refProperty])) {
                            return object[refProperty];
                        }
                    }
                }

	    	 	// convert to a map
	    	 	var javaMap = @java.util.LinkedHashMap::new()();
	    	 	// If it's a tree node, clean it up before converting otherwise we may end up serializing out
	    	 	// all parents and children!
                var treeProp = $wnd.isc.Tree.getPrototype().treeProperty;
                if (object[treeProp] != null) {
	    	 	    object = $wnd.isc.Tree.getCleanNodeData(object);
	    	 	}

                // remove SGWT backrefs if appropriate
                // see http://forums.smartclient.com/showthread.php?p=127912
                if (this._cleanSgwtProperties) {
                    delete object.__ref;
                    delete object.__module;
                }
	    	 	
	    	 	for (var fieldName in object) {
	    	 		// Not sure whether this could really happen
	    	 		if(!$wnd.isc.isA.String(fieldName)){
	    	 			continue;
	    	 		}

                    // Don't convert the GWT module created by BeanFactory
                    if (fieldName == $wnd.isc.gwtModule) continue;

                    var val = object[fieldName];
                    //if the field name is '__ref', the the value is already a GWT java object reference
                    var convertedVal = (fieldName == refProperty || this.isNativeJavaObject(val) ? val : $wnd.SmartGWT.convertToJavaObject(val, false, false));
 					@com.smartgwt.client.util.JSOHelper::doAddToMap(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)(javaMap, fieldName, convertedVal);
	    	 	}
	    	 	return javaMap;
	    	 }
        });

        // Given a GWT Java Object such as a java.lang.Integer, convert to the primitive type (an int) 
        // so we can manipulate the value directly in JavaScript
        $wnd.SmartGWT.convertToPrimitiveType = $entry(function (object) {
            if (object == null) return null;

            //With the exception of java.lang.String, touching any property on a GWT object ref like java.lang.Long or other GWT java class within JSNI causes
            //an exception to be raised in FF hosted mode
            //See http://code.google.com/p/google-web-toolkit/issues/detail?id=4946
            //The above issue seemed to have disappeared with a certain combination of FF, GWT and the GWT FF plugin. However it has now resurfaced with FF4.
            //Calling an API like $wnd.isc.isA.String(object) where object is a GWT Java object like java.lang.Long raises an exception
            //since the function $wnd.isc.isA.String tests (touches) properties on the object.
            //However calling 'typeof object' on a GWT object returns "function" (in FF4, Safari 5) or  "object" (in FF 3) and it does not raise an exception in FF hosted mode
            //Note that typeof on a java.lang.String returns 'string'

            //test to see if possibly a GWT primitive object type, and if so convert to its JS counterpart object, else treat it as a JS compatible object type
            // (eg GWT primitive int, boolean, long types)

            var objType = typeof object;
            if (objType == 'function' || objType == 'object') {
                if(@com.smartgwt.client.util.JSOHelper::isJavaNumber(Ljava/lang/Object;)(object)) return @com.smartgwt.client.util.JSOHelper::doubleValue(Ljava/lang/Number;)(object);
                if(@com.smartgwt.client.util.JSOHelper::isJavaBoolean(Ljava/lang/Object;)(object)) return object.@java.lang.Boolean::booleanValue()();
                if(@com.smartgwt.client.util.JSOHelper::isJavaDate(Ljava/lang/Object;)(object)) return @com.smartgwt.client.util.JSOHelper::convertToJavaScriptDate(Ljava/util/Date;)(object);
                //in certain browser versions, GWT hosted mode returns 'typeof obj' as object even for strings that originated from a object.toString() call in GWT java
                //see http://code.google.com/p/google-web-toolkit/issues/detail?id=4301 workaround this issue by explicitly casting to a String object
                if(@com.smartgwt.client.util.JSOHelper::isJavaString(Ljava/lang/Object;)(object)) return @com.smartgwt.client.util.JSOHelper::convertToString(Ljava/lang/Object;)(object);
            }

            return object;
        });

        if ($wnd.isc.RPCManager.__fireReplyCallback == null) {
            $wnd.isc.RPCManager.__fireReplyCallback = $wnd.isc.RPCManager.fireReplyCallback;
            $wnd.isc.RPCManager.fireReplyCallback = function (callback, request, response, data) {
            	// convert primitives (number / bool) to Objects before firing callbacks
            	if (data != null && $wnd.isc.isA.Number(data) || $wnd.isc.isA.Boolean(data)) {
            	    data = response.data = $wnd.SmartGWT.convertToJavaType(data);
            	}
            	return this.__fireReplyCallback(callback, request, response, data);
            }
        }        
    }-*/;

    private boolean hasUncaughtExceptions;

    public void onModuleLoad() {
        // added boolean init check flag because GWT for some reason invokes this entry point
        // class twice in hosted mode even though it appears only once in the load
        // hierarchy. Check with GWT team.
        if (!initialized) {
            init();
            I18nUtil.init();

            // install a default UEH that displays the error message in an alert when in
            // development mode so that is is not overlooked by the user during development
            GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

                public void onUncaughtException(Throwable t) {

                    String exceptionSummary = "Uncaught exception escaped: " +
                        t.getClass().getName() + "\n" + t.getMessage();

                    if (GWT.isScript()) {
                        // In production mode, log detailed exception content, 
                        // including stack traces, to the developer console.
                        if (t instanceof UmbrellaException) {
                            Set<Throwable> causes = ((UmbrellaException) t).getCauses();
                            Throwable[] exceptions = causes.toArray(new Throwable[0]);

                            String message = "";
                            for (int i = 0; i < exceptions.length; i++) {
                                if (i > 0) message += "\n";
                                message += SmartGwtExceptionUtil.toString(exceptions[i]);
                            }
                            SC.logWarn(message);
                        } else {
                            SC.logWarn(SmartGwtExceptionUtil.toString(exceptionSummary, t));
                        }
                    } else {
                        // In development mode, details are sent to the GWT development
                        // console (in Eclipse or equivalent) by the GWT.log call below.
                        Window.alert(exceptionSummary + "\nSee the GWT exception log for " +
                            "details.\nRegister a GWT.setUncaughtExceptionHandler(..) " +
                            "for custom uncaught exception handling."
                        );
                        // Unfortunately, all developer console logs show up in the GWT development 
                        // mode console as well.  So to avoid confusion and duplication, just log a
                        // heads-up message to the developer console to alert user to check Eclipse.
                        if (!hasUncaughtExceptions) {
                            SC.logWarn("GWT uncaught exceptions have been encountered.  " +
                                       "Check the Development Mode console for more details.");
                        }
                        // GWT.log no-ops in production mode
                        GWT.log("Uncaught exception escaped", t);
                    }

                   hasUncaughtExceptions = true;
                }
            });

            // Trigger generation of BeanFactories for any classes annotated with BeanFactory.Generate
            GWT.create(BeanFactory.AnnotationMetaFactory.class);

            initialized = true;
        }
    }
}
