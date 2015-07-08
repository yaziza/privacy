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

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.eclipse.recommenders.privacy.rcp.IConfigurationDialogFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Optional;

public final class PrivatePermission {

    private final PrivateDatum datum;
    private final Principal principal;
    private final String purpose;
    private final String policyUri;

    private final boolean suggestApproval;
    private final ApprovalType approvalType;
    private final ExtensionReader extensionReader;

    public PrivatePermission(PrivateDatum datum, Principal principal, String purpose, String policyUri,
            ApprovalType approvalType, ExtensionReader extensionReader) {
        this(datum, principal, purpose, policyUri, false, approvalType, extensionReader);
    }

    public PrivatePermission(PrivateDatum datum, Principal principal, String purpose, String policyUri,
            boolean suggestApproval, ApprovalType approvalType, ExtensionReader extensionReader) {
        this.datum = requireNonNull(datum);
        this.principal = requireNonNull(principal);
        this.purpose = requireNonNull(purpose);
        this.policyUri = requireNonNull(policyUri);

        this.suggestApproval = suggestApproval;
        this.approvalType = requireNonNull(approvalType);
        this.extensionReader = requireNonNull(extensionReader);
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

    public boolean isSuggestApproval() {
        return suggestApproval;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public boolean isAdvancedPreferencesSupported() {
        return extensionReader.isAdvancedPreferencesSupported(this);
    }

    public void openConfigurationDialog(Shell shell) {
        Optional<IConfigurationDialogFactory> factory = getConfigurationDialog();
        if (factory.isPresent()) {
            IConfigurationDialogFactory configurationDialog = factory.get();
            configurationDialog.open(shell);
        }
    }

    private Optional<IConfigurationDialogFactory> getConfigurationDialog() {
        return extensionReader.getConfigurationDialog(this);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }

        PrivatePermission that = (PrivatePermission) other;
        return Objects.equals(this.datum, that.datum) && Objects.equals(this.principal, that.principal)
                && Objects.equals(this.purpose, that.purpose) && Objects.equals(this.policyUri, that.policyUri)
                && Objects.equals(this.suggestApproval, that.suggestApproval)
                && Objects.equals(this.approvalType, that.approvalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datum, principal, purpose, policyUri, suggestApproval, approvalType);
    }
}
