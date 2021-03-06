/********************************************************************************
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.ecore.glsp.handler;

import java.util.List;

import org.eclipse.emfcloud.ecore.glsp.EcoreEditorContext;
import org.eclipse.emfcloud.ecore.glsp.EcoreRecordingCommand;
import org.eclipse.emfcloud.ecore.glsp.gmodel.GModelFactory;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.action.kind.AbstractOperationAction;
import org.eclipse.glsp.api.action.kind.RequestBoundsAction;
import org.eclipse.glsp.api.action.kind.SetDirtyStateAction;
import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.server.actionhandler.OperationActionHandler;

public class EcoreOperationActionHandler extends OperationActionHandler {

	@Override
	public List<Action> doHandle(AbstractOperationAction action, GraphicalModelState graphicalModelState) {
		EcoreModelState modelState = EcoreModelState.getModelState(graphicalModelState);
		EcoreEditorContext context = modelState.getEditorContext();

		if (operationHandlerProvider.isHandled(action)) {
			OperationHandler handler = operationHandlerProvider.getHandler(action).get();
			String label = handler.getLabel(action);
			EcoreRecordingCommand command = new EcoreRecordingCommand(context, label,
					() -> handler.execute(action, modelState));
			modelState.execute(command);
			GModelRoot newRoot = new GModelFactory(modelState).create();

			return List.of(new RequestBoundsAction(newRoot), new SetDirtyStateAction(modelState.isDirty()));
		}
		return List.of();
	}

}