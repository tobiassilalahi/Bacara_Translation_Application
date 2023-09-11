# Bacara 

Created by Bangkit team 
1. A0030168, Timotius Haniel, timotiushaniel
2. A0101072, Tobias Ivandito Margogo Silalahi, tobiassilalahi
3. C2042005, Muhammad Rizky Perdana, MuhRizkyPerdana
4. C0121271, Michelle Octavia Yolanda Sari, Michelleoctaviaa
5. M0030169, Thomas Ken Ronaldi, thomken10
6. M0101095, Gita Ayu Salsabila, GitaAyu06

### Deployed Link to install Bacara Application APK on Android Device:
Android APK: https://drive.google.com/drive/folders/1yKHIo8RpJhZZytF-UqEX7idi5ClhcXZ0
Bacara requires 23 MB to store in your local devices, and it requires internet connection to run. Bacara is only available to run on Android version 8.0 or newer.

## Introduction
Bacara is an Android application that provide sign language recognition, sign language game and live sign language translation from sign language gesture (SIBI) to speech in Bahasa. 

## Why?
Our team is concerned about Indonesian who suffer from hearing disability. There are currently 466 million people who suffer from it throughout the globe. In Indonesia, 5000 babies are born every year with hearing disability. In Indonesia, BISINDO and SIBI are two major sign language that is used for communication in the deaf and mute community. Our team is trying to help Indonesian to learn sign language, practice their sign language ability and give live translation from sign language to speech in Bahasa.

## How Bacara works?

# Intro Bacara

<p align="center">
  <img src="https://github.com/tobiassilalahi/Bacara_Translation_Application/blob/main/image_resource/1.%20Splash%20Screen.PNG">
</p>

User will be given a short Introduction to Bacara and it’s feature

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/3.%20Second%20On%20Boarding%20Page.PNG">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/4.%20Third%20On%20Boarding%20Page.PNG">
</p>

# Sign In Page

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/5.%20Sign%20In%20Page.PNG">
</p>

User is required sign in using their Bacara account to use the app and access their Bacara account.
If user do not have a Bacara account, user can create an account by click “CREATE NEW ACCOUNT” on the Sign In Page above.

# Sign Up Page

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/6.%20Sign%20Up%20Page.PNG">
</p>

User can sign up using the page above by creating username, password and provide their email address. User information will be stored in Bacara’s database powered by firebase.

# Home Page

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/7.%20Home%20Page.PNG">
</p>

By clicking the Home icon in the bottom left part of Bacara’s navigation tab, user will be directed to Bacara’s Home Page. In Home Page, 3 features will be displayed on user screen.

1.	Play Game

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/Edited%20Page/11.%20Play%20Game%20Page.PNG">
</p>

Play Game is a BETA feature to learn sign language. This feature has not been deployed to the application.
The idea is that this feature provides a random word selected from a sets of word which will be displayed on screen. User is given a task to perform a sign language gesture for that specific word in the detection page by Clicking the Translate buttom.
If user perform the correct sign language gesture, then the word that is displayed will change its color to green and generate another random word.
Else, the word that is displayed will stay in red color.

2.	Report Bug

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/Edited%20Page/12.%20Report%20Bug%20Page.jpeg">
</p>

Report Bug is a feature that allows user to report a bug that occurs in their experience using the Bacara App. By clicking the Report Bug icon, user will be directed to gmail where user can write an email to the Bacara team regarding a bug that they experienced.

3.	Library

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/8.%20Library%20Page.PNG">
</p>

Library is a feature that allows user to search for BISINDO gesture. By clicking the Library icon, user will be directed to https://pmpk.kemdikbud.go.id/sibi/pencarian where user can type the word of their intended BISINDO gesture. The webapp will provide a video on how to perform the gesture in BISINDO.

# Profile Page

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/9.%20Profile%20Page.PNG">
</p>

By clicking the profile icon in the bottom right part of Bacara’s navigation tab, user will be directed to Bacara’s Profile Page. In Profile Page, user can see their Bacara’s account details including, sign up date, username, and subscription status.

# Translate Page

<p align="center">
  <img src="https://github.com/GitaAyu06/Bacara_App/blob/main/Bacara%20Screen%20Shoot/Edited%20Page/10.%20Translation%20Page.jpeg">
</p>

By clicking the Translate icon in the middle part of Bacara’s bottom navigation tab, user will be directed to Bacara’s Translate Page.
 In Translate Page, user will be able to will be asked to give Bacara’s access to user’s phone camera. By doing so, user can perform sign language gesture based on SIBI and that gesture will be translated into text which will be displayed on user screen as well as speech in Bahasa. 
Bacara’s sign language recognition is develop using Tensorflow and deployed in tf.lite. Bacara also use text-to-speech API for generating a speech in Bahasa for each sign language that is recognize by Bacara’s Machine Learning model.

We created our own dataset for training and testing our machine learning model for Sign Language Translation in SIBI
Dataset: https://www.kaggle.com/salsabilayuganto/sibi-sistem-bahasa-isyarat-indonesia
