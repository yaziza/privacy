Contributing to Recommenders Privacy
====================================

Thanks for your interest in this project.

Source Code
-----------

The source code is available [here](http://git.eclipse.org/c/recommenders.incubator/org.eclipse.recommenders.privacy.git/).


Developer resources:
--------------------

Information regarding source code management, builds, coding standards, and more.

- https://projects.eclipse.org/projects/technology.recommenders.incubator

Contributor License Agreement:
------------------------------

Before your contribution can be accepted by the project, you need to create and electronically sign the Eclipse Foundation Contributor License Agreement (CLA).

- [Eclipse Foundation Contributor License Agreement](http://www.eclipse.org/legal/CLA.php)

Not sure whether there is a CLA for you on file already?
You can easily check this yourself using the [Contributor License Agreement Lookup Tool](https://projects.eclipse.org/user/cla/validate).

Search For and Fix Bugs
-----------------------

The Eclipse Code Recommenders Privacy project uses Bugzilla to track ongoing development and issues.

- [Browse issues](https://bugs.eclipse.org/bugs/buglist.cgi?product=Recommenders.Incubator&component=Privacy)
- [Create a new issue](https://bugs.eclipse.org/bugs/enter_bug.cgi?product=Recommenders.Incubator&component=Privacy)

Be sure to search for existing issue before you create another one.
Remember that contributions are always welcome!

Build Code Recommenders Privacy from Source:
-------------------------------------------

Building Code Recommenders Privacy from source is straight-forward.
But if some of the steps below fail – [let us know](https://dev.eclipse.org/mailman/listinfo/recommenders-dev "Developer Mailing List").

### Building on the Command Line

If you want to build Code Recommenders from the command line, you will need [Apache Maven](http://maven.apache.org/download.html), version 3.x.

    $ git clone http://git.eclipse.org/gitroot/recommenders.incubator/org.eclipse.recommenders.privacy.git
    $ cd org.eclipse.recommenders.privacy
    $ mvn clean install

That’s it.
After a few minutes wait, you should see a `BUILD SUCCESS`.
(The initial build may take a bit longer, as Maven automatically downloads anything required by the build.)

**Windows users:** Code Recommenders uses Unix-style newlines (LF) throughout and fails the build if it finds Windows-style newlines (CRLF).
Please make sure that your Git configuration has `core.autocrlf` set to `false` to prevent Git from changing newlines to Windows-style.
(Changing newlines is unnecessary, as the Eclipse IDE can handle either style.)

### Building within the Eclipse IDE

If you want to contribute to Code Recommenders yourself, we suggest that you use Eclipse.

We recommend setting up Eclipse using [Eclipse Oomph](http://www.eclipse.org/oomph/).
Oomph will setup an up-to-date Eclipse IDE, install required and useful plugins, and import Code Recommenders’ source ready to build.

![Selecting an Eclipse Product to install](../plain/CONTRIBUTING/oomph-product-selection.png)

To get started with Oomph, [download the installer](https://wiki.eclipse.org/Eclipse_Oomph_Installer#Installation) and execute it.
Select the Eclipse product and version you wish to install, e. g. `Eclipse Standard/SKDK` or `Eclipse IDE for Java Developers`.
From the list of preconfigured projects double-click `Code Recommenders/Privacy` and click on `Next`.

![Setting up Eclipse for work on Code Recommenders with Oomph](../plain/CONTRIBUTING/oomph-privacy.png)

Follow the instructions that ask you for your login data to Eclipse servers.
The installation may take several minutes.
The freshly installed Eclipse will open during installation and automatically configure itself to let you work on Code Recommenders.

You are now set up to contribute code of your own to Eclipse Code Recommenders.
To test your contributions, just start an Eclipse runtime via the `tools/ide.product` product configuration file residing in the `org.eclipse.recommenders.privacy` project.

Once you have done some changes to the code, you can submit your changes to Gerrit, where Code Recommenders committers can review your change.
To do so, right click on the `org.eclipse.recommenders.privacy` project and select `Team/Commit…`.
For the commit comment, provide the ID of the [Bugzilla](https://bugs.eclipse.org/bugs/buglist.cgi?product=Recommenders.Incubator&component=Privacy) issue you are working on in the format `Bug XXXXXX: Change description`.
(If you are not working on an Bugzilla issue, please consider opening a new issue first.)
Also, please click the second (Add Signed-off-by) and third (Compute Change-Id for Gerrit Code Review) button above the commit message field; this will automatically generate the necessary `Signed-off-by` and `Change-Id` headers.
(Do not be alarmed if the generated ID shows as a all zeros; a proper ID will be generated once you submit the change.)
When you have composed your commit message, click on `Commit`.
Next, right click on the `org.eclipse.recommenders.privacy` project again and select `Team/Remote/Push to Gerrit…`.
Select `refs/for/master` Gerrit branch, then click on `Finish`.
Congratulations, you have contributed your first change to Code Recommenders Privacy!

Other committers will look at your code and provide feedback.
Do not be alarmed if your change is not immediately merged; most changes require a bit of back-and-forth between contributors and committers.

Contact:
--------

Contact the project developers via the project's ["dev" list](https://dev.eclipse.org/mailman/listinfo/recommenders-dev "Developer Mailing List").

