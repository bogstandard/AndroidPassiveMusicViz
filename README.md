
<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]]()
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]


<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/bogstandard/AndroidPassiveMusicViz">
    <img src="https://raw.githubusercontent.com/bogstandard/AndroidPassiveMusicViz/master/src/main/ic_launcher-web.png?token=ACHMHCWGS6N2XHCUDAQFVKS4622QY" alt="Logo" width="120" height="120">
  </a>

  <h3 align="center">AudioMediaExperiment</h3>

  <p align="center">
    Synchronous ambient audio visualisation across old phones!
    <br />
    <a href="https://ericdaddio.co.uk/"><strong>By Eric D'Addio</strong></a>
</p>



<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)



<!-- ABOUT THE PROJECT -->
## About The Project

[![Visualiser Screenshot][product-screenshot]](https://twitter.com/oldnewstandard/status/1083896824916856832)

[ **[Watch the demonstration video on Twitter.](https://twitter.com/oldnewstandard/status/1083896824916856832)** ]

A passive music visualiser for music you have playing within device’s microphone range. Made for putting old android phones to use as an item of passive digital furniture. Uses the microphone to listen to music played on a users preferred device and renders a fullscreen music visualisation for decorative purpose.

**The majority of the work for this application was performed as part of my Computer Science degree at the University of Brighton**

### Rational
Lots of people, myself included, have a plethora of old phones they no longer use as their main device but keep hold of, often these old phones see little or no use, instead they sit idle gathering dust.

Visualisers for music are on the way out, Spotify removed this feature pre-2012 with no intention of bringing it back (Community.spotify.com, 2012). Using a visualiser application like iTunes will likely consume valuable screen estate harming productivity.

### Application
The application puts old phones to use by transforming them into on-desk visualisers for music which is being played aloud within the environment by utilising the device’s microphone. Allowing users a visualiser for their music without sacrificing screen estate on their main devices or having to play music from the phone rendering the visualisation. It also puts an old phone to a fun use, rather than being sat on the shelf. If the user has several old android phones they can daisy-chain them together to make a longer visualisation.

### Built With
* Android Studio
* Java (Android flavoured)

## How it Works

There's no interaction between the phones, they're ignorant of each other. The illusion of communication is performed with a simple time delay. Audio levels are stored in a continually updating array:

**A single phone:**
```
If music is suddenly stopped:
At 1 seconds [ 0, 4, 7, 11, 8, 4, 0]   becomes  [▁▂▃▅▄▂▁]
At 2 seconds [ 0, 0, 4, 7, 11, 8, 4]   becomes  [▁▁▂▃▅▄▂] 
At 3 seconds [ 0, 0, 0, 4, 7, 11, 8]   becomes  [▁▁▁▂▃▅▄]
Visualisation & Array moving left to right
```

**A pair of phones:**
```
At 1 seconds [ 0, 4, 7, 11, 8, 4, 0, 0, 0, 0, 11, 8, 4, 0] 
becomes (Phone @ Position 0) [▁▂▃▅▄▂▁] (Phone @ Position 1) [▁▁▅▄▂▁]

At 2 seconds [ 0, 0, 4, 7, 11, 8, 4, 0, 0, 0, 0, 11, 8, 4] 
becomes (Phone @ Position 0) [▁▁▂▃▅▄▂] (Phone @ Position 1) [▁▁▁▅▄▂]

At 3 seconds [ 0, 0, 0, 4, 7, 11, 8, 4, 0, 0, 0, 0, 11, 8] 
becomes (Phone @ Position 0) [▁▁▁▂▃▅▄] (Phone @ Position 1 )[▂▁▁▁▅▄]
```

### Build

1. Clone the repo
2. Open in Android Studio
3. Export as App


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.


<!-- CONTACT -->
## Contact

Eric D'Addio - [@oldnewstandard](https://twitter.com/oldnewstandard)

Project Link: [https://github.com/bogstandard/AndroidPassiveMusicViz](https://github.com/bogstandard/AndroidPassiveMusicViz)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
* [Img Shields](https://shields.io)
* [Choose an Open Source License](https://choosealicense.com)
* [BEST ReadMe Template](https://github.com/othneildrew/Best-README-Template/)




<!-- MARKDOWN LINKS & IMAGES -->
[build-shield]: https://img.shields.io/badge/build-passing-brightgreen.svg?style=flat-square
[contributors-shield]: https://img.shields.io/badge/contributors-1-orange.svg?style=flat-square
[license-shield]: https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square
[license-url]: https://choosealicense.com/licenses/mit
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/eric-daddio
[product-screenshot]: https://pbs.twimg.com/ext_tw_video_thumb/1083893331774586880/pu/img/hx8aWVfRomRjzBGO.jpg