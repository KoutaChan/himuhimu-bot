# himuhimu-bot (ひむひむちゃんbot)
- [ななみちゃんBOTの招待リンク](https://discord.com/api/oauth2/authorize?client_id=781323086624456735&permissions=8&scope=bot)
- [ひむひむちゃんBOTの招待リンク](https://discord.com/api/oauth2/authorize?client_id=924887155535380510&permissions=8&scope=bot)

# ビルド方法

ビルドは「gradle clean shadowJar」 または 「gradle clean build」でlibフォルダ直下にjarできます。

# ひむひむちゃんBOTの機能
- ニコニコ動画の再生
- ななみちゃんBOTの機能のほとんどの機能（移行作業中）
  - 地震情報
  - ミュージック機能
  - カジノ機能
  - 投票機能 (まだ追加されてません！)
- 読み上げ機能
  - とても不安定
- その他いろいろな機能

# TODO
- [x] ミュージック機能
- [x] カジノ機能の追加
- [ ] 投票機能
- [x] ファイルシステム
- [x] SQLITE用のクラスを作る
- [ ] (サブ) コードの効率化 && 再コード
- [ ] (サブ) 読み上げ機能をDLLから直接動かせるようにする 

## 使用しているもの
### ライブラリ
- [JDA](https://github.com/DV8FromTheWorld/JDA)
- [LavaPlayer](https://github.com/sedmelluq/lavaplayer) ([fork](https://github.com/Walkyst/lavaplayer-fork/))
- [Lombok](https://github.com/projectlombok/lombok)
- [SQLite-JDBC](https://github.com/xerial/sqlite-jdbc) 
- [Jsoup](https://github.com/jhy/jsoup)
- [JSON-java](https://github.com/stleary/JSON-java)
- [HtmlUnit](https://github.com/HtmlUnit/htmlunit)
