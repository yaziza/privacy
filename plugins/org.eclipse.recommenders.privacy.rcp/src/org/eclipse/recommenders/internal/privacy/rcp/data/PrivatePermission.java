/**
 * Copyright (c) 2014 Yasser Aziza.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yasser Aziza - initial implementation
 */
package org.eclipse.recommenders.internal.privacy.rcp.data;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.recommenders.privacy.rcp.IAdvancedPreferencesDialogFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Optional;

public class PrivatePermission {

    private final PrivateDatum datum;
    private final Principal principal;
    private final String purpose;
    private final String policyUri;

    private final ApprovalType approvalType;
    private final ExtensionReader extensionReader;

    public PrivatePermission(PrivateDatum datum, Principal principal, String purpose, String policyUri,
            ApprovalType approvalType, ExtensionReader extensionReader) {
        this.datum = checkNotNull(datum);
        this.principal = checkNotNull(principal);
        this.purpose = checkNotNull(purpose);
        this.policyUri = checkNotNull(policyUri);

        this.approvalType = checkNotNull(approvalType);
        this.extensionReader = checkNotNull(extensionReader);
    }

    public String getDatumId() {
        return datum.getId();
    }

    public String getDatumName() {
        return datum.getName();
    }

    public Image getDatumIcon() {
        return datum.getIcon().createImage();
    }

    public String getPrincipalId() {
        return principal.getId();
    }

    public String getPrincipalName() {
        return principal.getName();
    }

    public Image getPrincipalIcon() {
        return principal.getIcon().createImage();
    }

    public String getPurpose() {
        return purpose;
    }

    public String getPolicyUri() {
        return this.policyUri;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public boolean isAdvancedPreferencesSupported() {
        return extensionReader.isAdvancedPreferencesSupported(this);
    }

    public void openAdvancedConfigurationDialog(Shell shell) {
        Optional<IAdvancedPreferencesDialogFactory> factory = getAdvancedConfigurationDialog();
        if (factory.isPresent()) {
            IAdvancedPreferencesDialogFactory advancedConfigurationDialog = factory.get();
            advancedConfigurationDialog.open(shell);
        }
    }

    private Optional<IAdvancedPreferencesDialogFactory> getAdvancedConfigurationDialog() {
        return extensionReader.getAdvancedConfigurationDialog(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        PrivatePermission other = (PrivatePermission) obj;
        if (!datum.equals(other.datum)) {
            return false;
        }
        if (!principal.equals(other.principal)) {
            return false;
        }
        if (!purpose.equals(other.getPurpose())) {
            return false;
        }
        if (!policyUri.equals(other.getPolicyUri())) {
            return false;
        }
        if (!approvalType.equals(other.getApprovalType())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (datum == null ? 0 : datum.hashCode());
        result = prime * result + (principal == null ? 0 : principal.hashCode());
        result = prime * result + (purpose == null ? 0 : purpose.hashCode());
        result = prime * result + (policyUri == null ? 0 : policyUri.hashCode());
        result = prime * result + (approvalType == null ? 0 : approvalType.hashCode());
        return result;
    }
}
