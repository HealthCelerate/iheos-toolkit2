package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.simcommon.client.SimIdFactory;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteSiteCommand;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSiteRequest;

class DeleteSite implements ClickHandler {

	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;

	/**
	 * @param actorConfigTab
	 */
	DeleteSite(ActorConfigTab actorConfigTab) {
		this.actorConfigTab = actorConfigTab;
	}

	public void onClick(ClickEvent event) {
		if (this.actorConfigTab.currentEditSite == null) {
			new PopupMessage("Must choose site first");
			return;
		}
		if (!Xdstools2.getInstance().isSystemSaveEnabled()) {
			new PopupMessage("You don't have permission to delete a System in this Test Session");
			return;
		}

		if (SimIdFactory.isSimId(actorConfigTab.currentEditSite.getName())) {
			new PopupMessage("You cannot delete a simulator from this tool");
			return;
		}

		if (!actorConfigTab.currentEditSite.getOwner().equals(Xdstools2.getInstance().getTestSessionManager().getCurrentTestSession()) &&
				!PasswordManagement.isSignedIn) {
			new PopupMessage("You cannot delete a System you do not own");
			return;
		}

		if (PasswordManagement.isSignedIn) {
			deleteSignedInCallback.onSuccess(true);
		}
		else {
			PasswordManagement.addSignInCallback(deleteSignedInCallback);
			PasswordManagement.addSignInCallback(updateSignInStatusCallback);

			if (Xdstools2.getInstance().multiUserModeEnabled && !Xdstools2.getInstance().casModeEnabled) {
				deleteSignedInCallback.onSuccess(true);
//				((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
			} else {
				new PopupMessage("You must be signed in as admin");
			}


//			new AdminPasswordDialogBox(actorConfigTab.getTabTopPanel());

			//				PasswordManagement.rmSignInCallback(deleteSignedInCallback);
			//				PasswordManagement.rmSignInCallback(updateSignInStatusCallback);
		}
	}
	
	// Boolean data type ignored 
	AsyncCallback<Boolean> deleteSignedInCallback = new AsyncCallback<Boolean> () {

		public void onFailure(Throwable ignored) {
		}

		public void onSuccess(Boolean ignored) {
			new DeleteSiteCommand(){
				@Override
				public void onComplete(String result) {
					actorConfigTab.currentEditSite.changed = false;
					actorConfigTab.newActorEditGrid();
					actorConfigTab.loadExternalSites();
					((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
				}
			}.run(new DeleteSiteRequest(ClientUtils.INSTANCE.getCommandContext().withTestSession(actorConfigTab.currentEditSite.getTestSession().getValue()),
					actorConfigTab.currentEditSite.getName()));
		}

	};
	
	// Boolean data type ignored 
	AsyncCallback<Boolean> updateSignInStatusCallback = new AsyncCallback<Boolean> () {

		public void onFailure(Throwable ignored) {
			actorConfigTab.updateSignInStatus();
		}

		public void onSuccess(Boolean ignored) {
			actorConfigTab.updateSignInStatus();
		}

	};

}