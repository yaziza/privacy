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
package org.eclipse.recommenders.internal.privacy.rcp.dialogs;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.recommenders.internal.privacy.rcp.data.DatumCategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.Principal;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrincipalCategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivateDatum;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class PopupDialogTest {

    private static final String SWT_ID = "id";
    private static final String GROUP_BY_INTERESTED_PARTY_BUTTON_ID = "org.eclipse.recommenders.privacy.rcp.preferences.groupByInterestedParty";
    private static final String GROUP_BY_INFORMATION_BUTTON_ID = "org.eclipse.recommenders.privacy.rcp.preferences.groupByInformation";
    private static final String DISABLE_ALL_BUTTON_ID = "org.eclipse.recommenders.privacy.rcp.preferences.disableAll";
    private static final String ENABLE_ALL_BUTTON_ID = "org.eclipse.recommenders.privacy.rcp.preferences.enableAll";

    private Principal principal;
    private PrincipalCategory principalCategory;
    private PrivateDatum firstDatum, secondDatum;
    private DatumCategory firstDatumCategory, secondDatumCategory;
    private PrivatePermission firstPermission, secondPermission;

    private IPrivacySettingsService service;

    private PermissionApprovalDialog sut;

    @Before
    public void setUp() {
        service = mock(IPrivacySettingsService.class);
        // when(service.isActivated()).thenReturn(true);

        principal = new Principal("com.example.firstPrincipal", "some principal", "some principal description", null);
        firstDatum = new PrivateDatum("com.example.firstDatum", "first datum", "first datum description", null);
        secondDatum = new PrivateDatum("com.example.secondDatum", "second datum", "second datum description", null);
        firstPermission = new PrivatePermission(firstDatum, principal, "some purpose", "some uri");
        secondPermission = new PrivatePermission(secondDatum, principal, "some purpose", "some uri");

        firstDatumCategory = new DatumCategory(firstDatum);
        secondDatumCategory = new DatumCategory(secondDatum);
        principalCategory = new PrincipalCategory(principal);
    }

    public SWTBot createBot(Shell shell) {
        return new SWTBot(shell);
    }

    public PermissionApprovalDialog createSUT(Set<DatumCategory> datumSet, Set<PrincipalCategory> principalSet,
            Set<PrivatePermission> permissionSet) {
        PermissionApprovalDialog popupDialog = new PermissionApprovalDialog(new Shell(), service, datumSet,
                principalSet, permissionSet);
        popupDialog.setBlockOnOpen(false);
        popupDialog.open();
        return popupDialog;
    }

    @Test
    public void testDialogButtonsExists() {
        sut = createSUT(new HashSet<DatumCategory>(), new HashSet<PrincipalCategory>(),
                new HashSet<PrivatePermission>());
        SWTBot bot = createBot(sut.getShell());

        SWTBotButton enableAllButton = bot.buttonWithId(SWT_ID, ENABLE_ALL_BUTTON_ID);
        SWTBotButton disableAllButton = bot.buttonWithId(SWT_ID, DISABLE_ALL_BUTTON_ID);

        enableAllButton.click();
        disableAllButton.click();
    }

    @Test
    public void testPopupDialogEnableDisableAll() throws Exception {
        HashSet<DatumCategory> datumSet = Sets.newHashSet(firstDatumCategory);
        HashSet<PrincipalCategory> principalSet = Sets.newHashSet(principalCategory);
        HashSet<PrivatePermission> permissionSet = Sets.newHashSet(firstPermission, secondPermission);

        firstDatumCategory.addPermissions(firstPermission, secondPermission);
        principalCategory.addPermissions(firstPermission, secondPermission);

        when(service.isNeverApproved(firstDatum.getId())).thenReturn(true);
        when(service.isNeverApproved(secondDatum.getId())).thenReturn(true);

        sut = createSUT(datumSet, principalSet, permissionSet);
        SWTBot bot = createBot(sut.getShell());

        SWTBotTree tree = bot.tree(0);
        assertThat(tree.hasItems(), is(true));
        SWTBotTreeItem principalItem = tree.getTreeItem("some principal");
        SWTBotTreeItem firstDatumNode = principalItem.getNode("first datum");
        SWTBotTreeItem secondDatumNode = principalItem.getNode("second datum");

        SWTBotButton enableAllButton = bot.buttonWithId(SWT_ID, ENABLE_ALL_BUTTON_ID);
        enableAllButton.click();
        assertThat(principalItem.isChecked(), is(true));
        assertThat(firstDatumNode.isChecked(), is(true));
        assertThat(secondDatumNode.isChecked(), is(true));

        SWTBotButton disableAllButton = bot.buttonWithId(SWT_ID, DISABLE_ALL_BUTTON_ID);
        disableAllButton.click();
        assertThat(principalItem.isChecked(), is(false));
        assertThat(firstDatumNode.isChecked(), is(false));
        assertThat(secondDatumNode.isChecked(), is(false));
    }

    @Test
    public void testCollapsingAndExpandingTreeviewItems() {
        HashSet<DatumCategory> datumSet = Sets.newHashSet(firstDatumCategory);
        HashSet<PrincipalCategory> principalSet = Sets.newHashSet(principalCategory);
        HashSet<PrivatePermission> permissionSet = Sets.newHashSet(firstPermission, secondPermission);

        firstDatumCategory.addPermissions(firstPermission, secondPermission);
        principalCategory.addPermissions(firstPermission, secondPermission);

        when(service.isNeverApproved(firstDatum.getId())).thenReturn(true);
        when(service.isNeverApproved(secondDatum.getId())).thenReturn(true);

        sut = createSUT(datumSet, principalSet, permissionSet);
        SWTBot bot = createBot(sut.getShell());

        SWTBotTree tree = bot.tree(0);
        assertThat(tree.hasItems(), is(true));
        SWTBotTreeItem principalItem = tree.getTreeItem("some principal");
        SWTBotTreeItem firstDatumNode = principalItem.getNode("first datum");
        SWTBotTreeItem secondDatumNode = principalItem.getNode("second datum");

        principalItem.collapse();
        principalItem.expand();
        assertThat(principalItem.isChecked(), is(false));
        assertThat(firstDatumNode.isChecked(), is(false));
        assertThat(secondDatumNode.isChecked(), is(false));

        firstDatumNode.toggleCheck();
        assertThat(principalItem.isChecked(), is(true));
        assertThat(principalItem.isGrayed(), is(true));
        assertThat(firstDatumNode.isChecked(), is(true));
        assertThat(secondDatumNode.isChecked(), is(false));

        secondDatumNode.toggleCheck();
        assertThat(principalItem.isChecked(), is(true));
        assertThat(principalItem.isGrayed(), is(false));
        assertThat(firstDatumNode.isChecked(), is(true));
        assertThat(secondDatumNode.isChecked(), is(true));
    }

    @Test
    public void testSwitchGroupBy() {
        HashSet<DatumCategory> datumSet = Sets.newHashSet(firstDatumCategory, secondDatumCategory);
        HashSet<PrincipalCategory> principalSet = Sets.newHashSet(principalCategory);
        HashSet<PrivatePermission> permissionSet = Sets.newHashSet(firstPermission, secondPermission);

        firstDatumCategory.addPermissions(firstPermission);
        secondDatumCategory.addPermissions(secondPermission);
        principalCategory.addPermissions(firstPermission, secondPermission);

        when(service.isNeverApproved(firstDatum.getId())).thenReturn(true);
        when(service.isNeverApproved(secondDatum.getId())).thenReturn(true);

        sut = createSUT(datumSet, principalSet, permissionSet);
        SWTBot bot = createBot(sut.getShell());

        deselectDefaultSelection(bot, 1);
        SWTBotRadio informationButton = bot.radioWithId(SWT_ID, GROUP_BY_INFORMATION_BUTTON_ID);
        informationButton.click();
        assertThat(informationButton.isSelected(), is(true));

        SWTBotTree tree = bot.tree(0);
        assertThat(tree.hasItems(), is(true));
        assertThat(tree.getAllItems().length, is(2));

        SWTBotTreeItem firstDatumItem = tree.getTreeItem("first datum");
        SWTBotTreeItem secondDatumItem = tree.getTreeItem("second datum");

        SWTBotTreeItem firstDatumPrincipal = firstDatumItem.getNode("some principal");
        SWTBotTreeItem secondDatumPrincipal = secondDatumItem.getNode("some principal");

        firstDatumPrincipal.toggleCheck();

        assertThat(firstDatumItem.isChecked(), is(true));
        assertThat(firstDatumItem.isGrayed(), is(false));
        assertThat(firstDatumPrincipal.isChecked(), is(true));

        assertThat(secondDatumItem.isChecked(), is(false));
        assertThat(secondDatumPrincipal.isChecked(), is(false));

        deselectDefaultSelection(bot, 0);
        SWTBotRadio groupByButton = bot.radioWithId(SWT_ID, GROUP_BY_INTERESTED_PARTY_BUTTON_ID);
        groupByButton.click();
        assertThat(groupByButton.isSelected(), is(true));

        tree = bot.tree(0);
        assertThat(tree.hasItems(), is(true));
        assertThat(tree.getAllItems().length, is(1));

        SWTBotTreeItem principalItem = tree.getTreeItem("some principal");
        SWTBotTreeItem firstDatumNode = principalItem.getNode("first datum");
        SWTBotTreeItem secondDatumNode = principalItem.getNode("second datum");

        assertThat(principalItem.isChecked(), is(true));
        assertThat(principalItem.isGrayed(), is(true));
        assertThat(firstDatumNode.isChecked(), is(true));
        assertThat(secondDatumNode.isChecked(), is(false));
    }

    @Test
    public void testSuggestApproved() {
        HashSet<DatumCategory> datumSet = Sets.newHashSet(firstDatumCategory);
        HashSet<PrincipalCategory> principalSet = Sets.newHashSet(principalCategory);
        HashSet<PrivatePermission> permissionSet = Sets.newHashSet(firstPermission, secondPermission);

        firstDatumCategory.addPermissions(firstPermission, secondPermission);
        principalCategory.addPermissions(firstPermission, secondPermission);

        when(service.isNeverApproved(firstDatum.getId())).thenReturn(false);
        when(service.isNeverApproved(secondDatum.getId())).thenReturn(true);

        sut = createSUT(datumSet, principalSet, permissionSet);
        SWTBot bot = createBot(sut.getShell());

        SWTBotTree tree = bot.tree(0);
        assertThat(tree.hasItems(), is(true));
        SWTBotTreeItem principalItem = tree.getTreeItem("some principal");
        SWTBotTreeItem firstDatumNode = principalItem.getNode("first datum");
        SWTBotTreeItem secondDatumNode = principalItem.getNode("second datum");

        assertThat(principalItem.isChecked(), is(true));
        assertThat(principalItem.isGrayed(), is(true));
        assertThat(firstDatumNode.isChecked(), is(true));
        assertThat(secondDatumNode.isChecked(), is(false));
    }

    @Test
    public void testCategoriesWithoutPermissions() {
        HashSet<DatumCategory> datumSet = Sets.newHashSet(firstDatumCategory);
        HashSet<PrincipalCategory> principalSet = Sets.newHashSet(principalCategory);
        HashSet<PrivatePermission> permissionSet = Sets.newHashSet(firstPermission);

        when(service.isNeverApproved(firstDatum.getId())).thenReturn(false);

        sut = createSUT(datumSet, principalSet, permissionSet);
        SWTBot bot = createBot(sut.getShell());

        SWTBotTree tree = bot.tree(0);
        assertThat(tree.hasItems(), is(false));
    }

    @Test
    public void testPermissionWidgetText() {
        HashSet<DatumCategory> datumSet = Sets.newHashSet(firstDatumCategory);
        HashSet<PrincipalCategory> principalSet = Sets.newHashSet(principalCategory);
        HashSet<PrivatePermission> permissionSet = Sets.newHashSet(firstPermission, secondPermission);

        firstDatumCategory.addPermissions(firstPermission, secondPermission);
        principalCategory.addPermissions(firstPermission, secondPermission);

        when(service.isNeverApproved(firstDatum.getId())).thenReturn(false);
        when(service.isNeverApproved(secondDatum.getId())).thenReturn(true);

        sut = createSUT(datumSet, principalSet, permissionSet);
        SWTBot bot = createBot(sut.getShell());

        SWTBotTree tree = bot.tree(0);
        assertThat(tree.hasItems(), is(true));
        SWTBotTreeItem principalItem = tree.getTreeItem("some principal");
        SWTBotTreeItem firstDatumNode = principalItem.getNode("first datum");
        SWTBotTreeItem secondDatumNode = principalItem.getNode("second datum");

        assertThat(principalItem.getText(), is("some principal"));
        assertThat(firstDatumNode.getText(), is("first datum"));
        assertThat(secondDatumNode.getText(), is("second datum"));
    }

    /**
     * Deselects a radio button. Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=344484
     *
     * @param currentSelection
     *            The index of the radio button to deselect
     */
    private void deselectDefaultSelection(final SWTBot bot, final int currentSelection) {
        UIThreadRunnable.syncExec(new VoidResult() {
            @Override
            public void run() {
                @SuppressWarnings("unchecked")
                Matcher<Widget> matcher = allOf(widgetOfType(Button.class), withStyle(SWT.RADIO, "SWT.RADIO"));
                Button button = (Button) bot.widget(matcher, currentSelection);
                button.setSelection(false);
            }
        });
    }

    @After
    public void after() {
        sut.close();
    }
}
