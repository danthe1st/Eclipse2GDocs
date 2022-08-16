# Eclipse2GDocs
> Mirror a file opened in Eclipse IDE™ to Google Docs™

[![Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client](https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.svg)](http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=5549611 "Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client")

## Video example

[![Watch this Video on YouTube](https://i.ytimg.com/vi/1XhXMDK3_mk/mqdefault.jpg)](https://www.youtube.com/watch?v=1XhXMDK3_mk)

## Setup

### Installation
* Select `Help`>`Install New Software`<br/>
![image](https://user-images.githubusercontent.com/34687786/123937084-196fa100-d996-11eb-8105-108a32d94865.png)
* Click on `Add Software Site`<br/>
![image](https://user-images.githubusercontent.com/34687786/123937282-4b810300-d996-11eb-8d2a-cdc8805751dc.png)
* In the dialog, enter a name and `https://raw.githubusercontent.com/danthe1st/eclipse-update-site/master/` as the URL<br/>
![image](https://user-images.githubusercontent.com/34687786/123937393-66ec0e00-d996-11eb-88ad-a0181644ae6f.png)
* Select the created Software Site under `Work With` and unselect `Group Items by category` <br/>
![image](https://user-images.githubusercontent.com/34687786/123937461-7c613800-d996-11eb-81f5-7b366790509e.png)
* Select `Eclipse2GDocs` and click on `Next`
* Complete the installation process

### Usage
* Open the file you want to mirror to Google Docs in Eclipse (with this plugin installed).
* Click the button `Mirror this file to a Google Document`.<br/>
![image](https://user-images.githubusercontent.com/34687786/123836876-7aea2e00-d90a-11eb-9c44-b79214616595.png)
* The default browser should open automatically. Authorize the application to access Google Docs.
* Open the Google Document to mirror to in a web browser and copy the document ID.<br/>
![image](https://user-images.githubusercontent.com/34687786/123838090-e2ed4400-d90b-11eb-8459-4fd418a71ff4.png)
* Enter the document ID in the respective prompt in eclipse.<br/>
![image](https://user-images.githubusercontent.com/34687786/123838825-b38b0700-d90c-11eb-8718-d9a9cd9a579c.png)
* The content of the opened file should be copied to the google document and automatically updated.

### Setup Development Environment
* Install the Eclipse plug-in [Eclipse PDE](https://marketplace.eclipse.org/content/eclipse-pde-plug-development-environment) from the eclipse marketplace: [![Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client](https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.svg)](http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=2234530 "Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client")
* Install the plug-in `m2e PDE - Maven Integration for Eclipse Plugin Development` from [https://download.eclipse.org/technology/m2e/releases/latest/](https://download.eclipse.org/technology/m2e/releases/latest/) to Eclipse.
* Import this project as well as its child projects (Maven Modules) as Maven Projects in Eclipse
* Open the file `plugin.xml` in the `io.github.danthe1st.eclipse2gdocs` project and click on the run button on the top right in order to start eclipse with this plugin.<br/>
![image](https://user-images.githubusercontent.com/34687786/123833918-25605200-d907-11eb-8b07-2a3954218f32.png)


### Privacy
All data obtained by Eclipse2GDocs is kept locally and not shared with any third-party services.

It uses the Google Docs API in order to write to a Google Document upon a users request.