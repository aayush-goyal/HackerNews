A HackerNews Android App.

# Screenshots
<table>
	<tr>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Launcher%20Activity.png"></td>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Sign%20In%20Activity.png"></td>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Phone%20Number%20Fragment.png"></td>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Phone%20Number%20Verification%20Fragment.png"></td>
	</tr>
    <tr>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/New%20User%20Details%20Fill%20Up%20Activity.png"></td>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Main%20Activity.png"></td>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Article%20Fragment.png"></td>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Article%20Comments%20Fragment.png"></td>
   	</tr>
	<tr>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Profile%20Activity.png"></td>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/Update%20User%20Details%20Activity.png"></td>
    	<td><img src="http://projects.aayushgoyal.in/hackernews/About%20App%20Activity.png"></td>
  	</tr>
</table>


# Changelog
### [1.0.0] - 2019-02-09
---
#### Added
- Launcher Screen that makes certain checks on initial app load like checking for sign-in sessions, etc. for smooth user experience.
- Sign In Screen that navigates the user to Phone Auth Screen to sign-in/sign-up using phone number verification.
- Phone Auth Screen that lets user to sign-in/sign-up using phone number authentication. It contains two fragments:
  1. Phone Number Screen that asks user to enter his phone number that he wishes to provide for sign-in/sign-up process.
  2. Phone Number Verification Screen that waits for automatically detecting an OTP or allows user to enter it manually in case it fails to identify it automatically and upon entering of OTP the authentication process and in-turn sign-in/sign-up process is completed.
- New User Details Fill Up Screen that lets the user fill up his basic details if he is signing-up or has signed-in previously but didn't enter the basic details required by the app.
- Main Screen that displays top 20 stories from HackerNews platform including the score, time, user, number of comments for each story.
- Article Screen that contains two fragments:
  1. Article Fragment that shows the main content of the story inside a WebView.
  2. Article Comments Fragment that displays all the comments of the story in a RecylerView.
- Profile Screen that displays all the basic details about the user along with providing some key functionalities to the user that he can perform like updating and displaying profile picture, signing-out of the app, and etc.
- Update User Details Screen that lets user edit and save the basic details about him.
- About App Screen that shows basic info about the app and developer.
- App launcher icon for the app.

#### Changed
- Menu icons in app bar. Now, the app bar shows "Profile" and "App Info" icons.

#### Removed
- Google Sign In button from Sign In Screen as the app does not support that functionality.

### [0.2.0] - 2018-11-27
---
#### Added
- Completed UI design for Sign-In screen.

#### Changed
- Changed default app font to [Montserrat](https://fonts.google.com/specimen/Montserrat).

#### Removed
- Removed boilerplate resources.

### [0.1.0] - 2018-11-17
---
#### Added
- First commit.


## LICENSE

Copyright 2018 Aayush Goyal

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.