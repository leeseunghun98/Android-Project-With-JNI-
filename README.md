# Android-Project-With-JNI
Android application with Java Native Interface

## Skills

<img src="https://img.shields.io/badge/Java-F9AB00?style=for-the-badge&logo=Java&logoColor=white"><img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white"><img src="https://img.shields.io/badge/CMake-064F8C?style=for-the-badge&logo=CMake&logoColor=white">

## CMake? JNI?

FPGA Board에서 원활한 동작을 위해 즉, 다양한 운영체제에서 빌드하기 위해 이용

JNI는 CMake가 C로 작성되기 때문에 이를 Application의 Java 코드와 함께 이용하기 위함

## Content

FPGA Board를 이용한 퍼즐 게임 Application

사진 촬영 후 퍼즐의 가로와 세로 크기 정하기

![image](https://user-images.githubusercontent.com/78264248/196934469-1a56d3e3-26e1-4b8b-819a-fcad7ba9d7c6.png)

정한 사진 만큼 퍼즐을 수행

![image](https://user-images.githubusercontent.com/78264248/196934583-a06aebef-2945-41ab-8840-4f4ecc66f3fd.png)

7-Segment는 남은 기회를 뜻하며, 현재 36번의 기회가 남음, 현재 정답이 아닌 위치에 있는 퍼즐의 수 = 켜진 LED의 수 = 2

![image](https://user-images.githubusercontent.com/78264248/196934668-94161b3f-76f1-49bb-b936-b152691dcc73.png)

12회를 움직여 퍼즐을 완성, 켜진 LED의 수 = 0

![image](https://user-images.githubusercontent.com/78264248/196934758-e6761aa5-2506-4139-88c3-ee3f0317b704.png)
