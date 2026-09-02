**The Current Smart AAC**
Developed in Android Studio/Google AI Studio in Java (Kotlin), uses the OpenBoard library for symbols for the cards(https://www.openboardformat.org/docs). Used AI to build the main framework, then began implementing the developer considerations from openaac.org (and features I personally want to pursue, such as customizing the layout for ease of use): https://www.openaac.org/considerations
recent additions 9/1: 
-cleaned up comments
-fixed camera functionality (now works when app is downloaded onto device, not on preview)
-migrated front-end to a responsive React architecture with OpenBoard support, emotion relevance sorting, and native bridge integration
**The camera-based emotion detection is currently being tested, so I have added buttons to test the different user emotions and see how well the AAC chart re-arranges itself.**
Smart AAC is a new AAC project designed to incorporate emotion recognition to the AAC framework. The pipeline involves the user displaying their face to a live camera, which extracts the user's facial features-eyebrow furriness, mouth, eye openness, and several more traits-and matches that with the relevant emotion. Then, it sorts the remaining AAC cards by relevance to the emotion. Finally, it suggests new words for the user using Gemini API. 
