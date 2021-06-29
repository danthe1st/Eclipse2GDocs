# Eclipse2GDocs
> Mirror a file opened in Eclipse to Google Docs

## Setup

### Installation
* Install the Eclipse plug-in [Eclipse PDE](https://marketplace.eclipse.org/content/eclipse-pde-plug-development-environment) from the eclipse marketplace: [![Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client](https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.svg)](http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=2234530 "Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client")
* Install the plug-in `m2e PDE - Maven Integration for Eclipse Plugin Development` from [https://download.eclipse.org/technology/m2e/releases/latest/](https://download.eclipse.org/technology/m2e/releases/latest/) to Eclipse.
* Open this project in Eclipse
* Open the file `TargetDefinition.target`.
* Click `Set as Active Target Platform` on the top right and wait until it finishes loading.<br/>
![image](https://user-images.githubusercontent.com/34687786/123833663-e0d4b680-d906-11eb-845e-86cf4c0a0bac.png)
* Open the file `plugin.xml` and click on the run button on the top right in order to start eclipse with this plugin.<br/>
![image](https://user-images.githubusercontent.com/34687786/123833918-25605200-d907-11eb-8b07-2a3954218f32.png)

### Usage
* Before using Eclipse2GDocs the first time, you need to [create a Google OAuth2 application and create Desktop application credentials](https://developers.google.com/workspace/guides/create-credentials). The scope `https://www.googleapis.com/auth/documents` is required.
* This application needs to be configured to be able to access the Google Docs API. This can be done [here](https://console.cloud.google.com/apis/library/docs.googleapis.com?q=Google%20Docs%20API).
* Open the file you want to mirror to Google Docs in Eclipse (with this plugin installed)
* Click the button `Mirror this file to a Google Document`.<br/>
![image](https://user-images.githubusercontent.com/34687786/123836876-7aea2e00-d90a-11eb-9c44-b79214616595.png)
* Enter the previously obtained client ID and client Secret in the prompt.<br/>
![image](https://user-images.githubusercontent.com/34687786/123838345-32cc0b00-d90c-11eb-83fe-590b8792c565.png)<br/>
![image](https://user-images.githubusercontent.com/34687786/123838502-5abb6e80-d90c-11eb-801d-fa0df18fe93b.png)
* The default browser should open automatically. Authorize the application to access Google Docs.
* Open the Google Document to mirror to in a web browser and copy the document ID.<br/>
![image](https://user-images.githubusercontent.com/34687786/123838090-e2ed4400-d90b-11eb-8459-4fd418a71ff4.png)
* Enter the document ID in the respective prompt in eclipse.<br/>
![image](https://user-images.githubusercontent.com/34687786/123838825-b38b0700-d90c-11eb-8718-d9a9cd9a579c.png)
* The content of the opened file should be copied to the google document and automatically updated.
