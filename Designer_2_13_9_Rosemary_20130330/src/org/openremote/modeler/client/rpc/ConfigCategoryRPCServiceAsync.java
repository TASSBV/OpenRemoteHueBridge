package org.openremote.modeler.client.rpc;

import java.util.Set;

import org.openremote.modeler.domain.ConfigCategory;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConfigCategoryRPCServiceAsync {
   public void getCategories(AsyncCallback<Set<ConfigCategory>> callback);
}
