# video-nas

Video nas is a small script to stream video files on an Android TV.

## Backend

The backend of this service provides a simple command to share the current working directory with the android TV.

### Installation


Before installation make sure npm and nodejs are installed (https://docs.npmjs.com/cli/v7/configuring-npm/install).

1. Clone this repository (`git clone https://github.com/Ondolin/video-nas.git`)
2. Move into the backend folder (`cd video-nas/backend`)
3. Install npm dependencies (`npm install`)
4. Install the video-nas cli tool (`npm install -g .`)
5. move to the directory you want to steam
6. run the cli tool (`video-nas`)


TIPP: You can stop the tool by hitting CTRL+C

## Android TV App

The android TV app will show the files of the shared folder. There you can select a video you want to view. By clicking on it in the list a video playback will start.

### Installation

1. Allow "install Apps from unknown sources" in the security settings of your android TV
2. move the current apk release to a USB device
3. Connect the USB device to the TV
4. Follow the installation instructions
