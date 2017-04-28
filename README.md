Map study
=========

### Understand how map is rendered
- Use Open Street Map and use unity to render
- Understand OSM map data structure
- Understand Unity data structure
- Code file reader/parser, and simple renderer for testing
- Code converter for Unity, and demo app

### Key words and Abgreviations
Words|Description
-----|-----------
[OSM](http://www.openstreetmap.org) | Open Street Map
[MVT](https://www.mapbox.com/vector-tiles/) | Mapbox Vector Tiles. [spec](https://www.mapbox.com/vector-tiles/specification/)
[PBF](https://developers.google.com/protocol-buffers/?hl=en) | Protocol Buffers. Google's language-neutral, platform-neutral, extensible mechanism for serializing structured data. [spec](https://developers.google.com/protocol-buffers/docs/encoding)

### Blogs
- [Vector tiles remixed](http://gdunlop.github.io/Vector-tiles-remixed/) Vector tiles creation from OSM and rendering.
- [MapzenGo](http://barankahyaoglu.com/dev/category/unity3d/) PockemonGo clone by Unity3d with OSM.
- [PockemonGo clone](http://qiita.com/JunSuzukiJapan/items/de3ffc2ab490bd21a09a) How to make PockemonGo clone on Qiita.

### Services
- [OSM Data Extracts](http://download.geofabrik.de) | OSM data in regions via PBF
- [Mapzen](https://mapzen.com) Mapping platform to display, search, and navigate.
- [CARTO](https://carto.com) Platform for discovering and predicting the key insights underlying the location data.

### Tools
- [osmosis](http://wiki.openstreetmap.org/wiki/Osmosis) Command line Java application for processing OSM data. [github](https://github.com/openstreetmap/osmosis)
- [TileMaker](http://blog.systemed.net/post/13) Command line tool to create vector tiles. [github](https://github.com/systemed/tilemaker)
- [iD](https://www.openstreetmap.org/edit?editor=id) In-browser OSM editor.
- [JOSM](https://josm.openstreetmap.de) OSM editor for Java 8.
- [QGIS](http://www.qgis.org/en/site/) Geographic Information System.
- [OSM2World](http://wiki.openstreetmap.org/wiki/OSM2World) Converter that creates a 3D model of the world based on OSM. [github](https://github.com/tordanik/OSM2World).
- [ActionStreetMap](https://actionstreetmap.github.io/demo/) Framework for building real city environment using Unity3d and OSM. [github](https://github.com/ActionStreetMap/demo).



## Routing
### Wiki/Blogs
- [OSM Wiki](http://wiki.openstreetmap.org/wiki/Routing)

### Tools
- [SimpleOsmRouter](git@github.com:F6F/SimpleOsmRouter.git)


## Others
- [ActionStreetMapを使って自分の住んでいる街をUnityで再現する](http://lanius.hatenablog.jp/entry/2014/11/27/221607)
- [utymap](https://github.com/reinterpretcat/utymap) new ActionStreetMap. C++ cross platform library, not only Unity3d.
- [Visitor pattern](http://www.techscore.com/tech/DesignPattern/Visitor.html/)
- [Boost Qi](https://boostjp.github.io/tips/parser.html) Parser generator.
- [multipolygon](http://wiki.openstreetmap.org/wiki/Relation:multipolygon) OSM Wiki Relation:multipolygon
- [Relation Check](http://wiki.openstreetmap.org/wiki/Relation_Check)
- [canvasでドロネー三角形分割を描く](http://blog.webcreativepark.net/2015/10/22-060729.html)
- [OpenGL Tessellation](http://www.songho.ca/opengl/gl_tessellation.html) Required to draw concave polygons or polygons with intersecting edges into convex polygons in OpenGL.
- [Polygon Tessellation in JOGL](https://www.java-tips.org/other-api-tips-100035/112-jogl/1666-polygon-tessellation-in-jogl.html)
- [C#6.0時代のUnity](http://qiita.com/divideby_zero/items/71a38acdbaa55e88e2d9) .NET 4.6 supported in Unity 2017. C# 6.0 available.
- [Unity入門に最適なチュートリアルサイトまとめ・比較](https://mayonez.jp/805)
- [UnityのエディタとしてJetBrainsのRiderを使う](http://mizoguche.info/2016/09/rider_with_unity/)
- [Unity を使いはじめたばかりの頃の自分に伝えたい、Unity の基本 【2014年版】](http://d.hatena.ne.jp/komiyak/20141216/1418760578)
- [Painting with Code](http://airbnb.design/painting-with-code/) Introducing our new open source library React Sketch.app.
- [React Nativeとネイティブアプリでの開発の違いとは](https://techacademy.jp/magazine/11652)
- [新しくなったFirebase Unity SDKが登場](https://pigbo.co/新しくなったfirebase-unity-sdkが登場-29d71370dcc8)

## Notes
### yeild in Unity C#
'yeild' is used in coRoutine. It stops operation and resumes in next frame. [Qiita](http://qiita.com/kazz4423/items/73219068684e87adc87d)
```
yeild return null; // Stops for 1 frame and resume.

yeild return new WaitForSeconds( num ); // Stops for num seconds and resume.
```

### OSM data structure
- XML format (or PBF in hosts)
- Many hosts offer compiled data. Assuming because the original data needs some processs like combining fragmented paths of an area, cascading attributes from different layers of data, and splitting data into smaller regions/area. The sample app directly uses the original data, but it should be pre-processed for performance.
- Three type of elemnts, Node, Way and Relation. Node is a point, Way is a combination of Nodes creating line, or area if circled, and Relation is to combine elements to build complicated buildings, lines, area, etc.
- An area can consists of multiple ways, thus need to combine them on drawing as OpenGL Polygon with filling color.
- An area can be self-intersecting polygon and/or concave polygon. Such polygon needs to be tessellated.

### OpenGL
- Projection mode and ModelView mode
- Projection is set in Projection mode, such as window size, perspective or ortho.
- Modeling is set in ModelView mode, even camera rotation is done (same as whole world rotation).
- LoadIdenty() is to reset and is done at first in every frame (possible to keep using but easier to reset).


