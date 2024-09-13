# DroidKaigi 2024 Sample Detuned App

本アプリはDroidKaigi 2024で株式会社サイバーエージェントが展示するチューニング企画のお題として実装したアプリです。
本企画は2024/8に株式会社サイバーエージェントが学生向けに開催した架空のアプリケーションのチューニングコンペ「CYCOMPE」に着想を得ています。

<img width="256" src="https://github.com/user-attachments/assets/47599f35-5187-4fc1-88ee-2e85212f017d"/>

## アプリの機能説明

バックエンドAPIに登録したイベントからアルバムデータを取得し、その画像の閲覧、保存が行えるアプリです。
本アプリは以下の画面を持っています

### トップ画面

バックエンドAPIから取得するイベントを指定する画面です。対象となるホスト名、イベントID、アクセスコードを指定することで、そのイベントが存在する場合はアルバム画面に遷移します。
デフォルトで以下の設定がされており、デモ用のイベントにアクセスすることが可能です。

- ホスト名：https://photospeedway.dokup.dev
- イベントID：1
- アクセスコード：demo_access_code

また、存在したイベントはRoomを介して端末内DBに保存され、次からは入力をスキップしてアルバムにアクセスすることができるようになります。

### アルバム画面

指定したイベントのアルバムを取得します。
アルバムの各画像は、Save this photoボタンによって端末ローカルに保存することができます。
また、画像をタップするとその画像の詳細画面に遷移します。

### 詳細画面

指定した画像のExif情報を表示します。
この画面でもSave this photoボタンによって端末ローカルに保存することができます。

## ブランチの説明

- main: 特に何も意識していないブランチです
- detuned: 意図的にアプリにパフォーマンス的な改善点を仕込んだブランチです

## リポジトリの構造について

アプリはappモジュール単一で実装されています。
com.example.albumパッケージ以下は下記の構造となっています。

- di: Dagger HiltでのDIに必要なモジュール
- infra
  - datasource
    - api: retrofit + okhttpでのWeb APIアクセスを行うコード
    - db: Roomでの端末内DBアクセスを行うコード
    - file: 端末へのファイル保存を行うコート
  - repository: Repository層が実装されています
- misc: 拡張関数など、Util的に使いたいコード
- model: ドメインモデルのデータ構造を記したコード
- ui
  - album: アルバム画面に関連するUIコード（ViewModel, State, Screen）
  - detail: 詳細画面に関連するUIコード（ViewModel, State, Screen）
  - top: トップ画面に関連するUIコード（ViewModel, State, Screen）
  - navigation: Compose Navigationに関連するコード
  - theme: Compose Themingに関連するコード
