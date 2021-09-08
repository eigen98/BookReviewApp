# BookReviewApp   
### 도서리뷰앱    

인터파크 Open API 를 통해 베스트셀러 정보를 가져와서 화면에 그릴 수 있음.   

인터파크 Open API 를 통해 검색어에 해당하는 책 목록을 가져와서 화면에 그릴 수 있음.   

Local DB 를 이용하여 검색 기록을 저장하고 삭제할 수 있음.   

Local DB 를 이용하여 개인 리뷰를 저장할 수 있음.   

![KakaoTalk_20210908_192639661](https://user-images.githubusercontent.com/68258365/132493343-ed7d311c-c98a-4ef1-8c2f-df95fd94e8f8.jpg)



### 인터파크 도서 Open API 신청하기   
  포스트맨을 활용하여 데이터받아보기   
  ->api를 실제로 요청하고 반환되는 결과값을 쉽게 보여주는 기능(안드로이드 실행 안 하고 보기 가능)   
### 도서 리스트 화면 - Open API를 통해 도서 목록 가져오기   
  Retrofit 사용하기 (API 호출)    
  -> Retrofit이란 TypeSafe한 HttpClient라이브러리   
  -> 바로 네트워크로 부터 전달된 데이터를 우리 프로그램에서 필요한 형태의 객체로 받을 수 있다.   
  ##### 추가 방법   
  implementation 'com.squareup.retrofit2:retrofit:2.9.0'   
	implementation 'com.squareup.retrofit2:converter-gson:2.9.0' //gson으로 변환해주는 converter담당 라이브러리. 조금더 직렬화   
  #### retrofit구성   
  api패키지 만든 후 -> bookService 인터페이스 생성  
  bestseller, search 각각 두개의 api를 Get방식으로 가져옴   
  
  interface BookService {

    @GET("/api/search.api?output=json")     //(baseUrl 뒤에있는 부분,요청내용인 거 같다.) API가져오는 형식 GET형식 : 데이터를 요청했을 때 반환해주는 HTTP형식,
    fun getBooksByName(
        @Query("key") apiKey : String,
        @Query("query") keyword : String
    ) : Call<SearchBookDto> <모델클래스>

    @GET("/api/bestSeller.api?output=json&categoryId=100") //(요청내용url인 거 같다.)
    fun getBestSellerBooks(
        @Query("key") apiKey: String
    ): Call<BestSellerDto>  //<모델클래스>

  }


  POST : 요청할 때 새롭게 만들때 create할 때 , 데이터가 좀더 크기에 HTTP body에 넣어서 전달하는 방식
  
  다음은 <모델 클래스> 생성해준다
  
  data class Book(
  //들어가야할 데이터, @SerializedName을 이용해 이름 매칭
    @SerializedName("itemId") val id : Long,
    @SerializedName("title") val title : String,
    @SerializedName("description") val description : String,
    @SerializedName("coverSmallUrl") val coverSmallUrl : String
)

북에대한 **Entity**모델은 완성을 했지만 아이템 밖에있는 전체 데이터가 필요. (DTO 추가)

참고)Entity란 실체, 객체라는 의미로 실무적으로는 엔티티 라고 부름   
학생이라는 엔티티는 학번, 이름 , 학점, 등록일자 등의 속성으로 특정지어짐.   

전체모델에서 데이터를 꺼내올 수 있게 하는 기능을 **Dto**라고함   
참고)DTO란 (Data Transfer Object) 계층 간 데이터 교환역할을 함. Entity를 Controller같은 클라이어단과 직접   
마주하는 계층에 직접 전달하는 대신 DTO를 사용해 데이터를 교환함.   

	  data class BestSellerDto( //
	      @SerializedName("title") val title : String,
	      @SerializedName("item") val books : List<Book>,
	  )

	  data class SearchBookDto(
	      @SerializedName("title") val title : String,
	      @SerializedName("item") val books : List<Book>
	  )

콜되는 responseAPI 에 모델DTO 추가

retrofit인터페이스를 작성하였으니 이제 retrofit 구현체를 구현해야한다.
onCreate쪽에 구현   
	  val retrofit = Retrofit.Builder()
			  .baseUrl("https://book.interpark.com")
			  .addConverterFactory(GsonConverterFactory.create()) //Gson으로 변환해주는 converter
			  .build()  //구현체 생성

	  val bookService = retrofit.create(BookService::class.java) //구현체//retrofit 인터페이스의 구현체 생성
	  bookService.getBestSellerBooks(getString(R.string.interparkAPIKey)) //구현한 북서비스를 이용하여 메소드 호출. 인자로는 API키를 받음
			  .enqueue(object : Callback<BestSellerDto>{  //큐에 넣어줌 //반환값은 콜백
			      override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {  // 콜백에 구현이 필요한 부분 구현 //API요청 성공시 호출
				  //성공처리
				  if(response.isSuccessful.not()){
				      Log.d(TAG, "NOT! SUCCESS")
				      return
				  }
				  response.body()?.let{ //reponse에서 바디를 꺼냄//바디는 bestsellerdto데이터가 들어있음.// 바디가 없을 수도 있기에 ? 
				      Log.d(TAG,it.toString())

				      it.books.forEach{book ->
					  Log.d(TAG, book.toString())
				      }
				      adapter.submitList(it.books) //반응이 오면 리스트를 대체해줌
				  }


			      }

			      override fun onFailure(call: Call<BestSellerDto>, t: Throwable) { //API요청 실패시 호출

				  //실패처리
				  Log.d(TAG, t.toString())
			      }

			  })

하지만 앱이 죽는다 Permission denied 인터넷 권한을 불러와서 해결한다. manifest에서 추가해준다.
이번엔 또 실패한다. 그 이휴는not permitted by network security policy ->이것은 baseUrl이 http형식이기때문에 평문으로 전송하는 옛날 프로토콜이기에 안드로이드에서 막힌 것이다.
서버에 https로 연결하는 방법이 있고 두번 째 방법은 http연결을 허용하는 방법이다. 우선 baseUrl을 https로 바꿔서 해결하였다.

                
                

### 도서 리스트 화면 - RecyclerView 활용하여 아이템 그려보기
  리사이클러뷰를 그려주기위해서 뷰를 그려줄 레이아웃으로 가서 리사이클러뷰를 추가해준다.  
  -> 우선 layoutManager와 Adapter가 필요.
    그전에 뷰 바인딩에 대해서 알아야한다.
    참고) 뷰 바인딩(View Binding) 은 뷰와 상호 작용하는 코드를보다 쉽게 작성할 수있는 기능입니다
            뷰 바인딩을 사용하는 것으로 findViewById 메서드를 대체할 수 있습니다.
            
            **뷰바인딩 추가**
            안드로이드 4.0 부터는 data binding 과 view binding 을 사용하기 위해서는 사용 선언 방법이 조금 변경 되었다고 합니다.
                android {
                    buildFeatures {
                          viewBinding = true
                     }
                }
            
                
                
     **어답터 생성** 
     //DiffUtil은 RecyclerView의 성능을 한층 더 개선할 수 있게 해주는 유틸리티 클래스다. 
     //기존의 데이터 리스트와 교체할 데이터 리스트를 비교해서 실질적으로 업데이트가 필요한 아이템들을 추려낸다
     class BookAdapter : ListAdapter<Book,BookAdapter.BookItemViewHolder>(diffUtil) { //ListAdapter상속 제네릭은<북, 뷰홀더>() //원래는 <뷰 홀더>였는데... 뭐지??
      // <Place, PlaceViewHolder>라고함. ListAdapter라 ListView와 함께 써야할 것 같은 느낌이 들지만 아니다) 얘를 사용하면 RecyclerView Adapter 코드가 엄청 짧아진다.

      inner class BookItemViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root){ // 인자를 뷰로 받기 위해 뷰 바인딩 사용
          //레이아웃 item_book(그려줄아이템뷰)을 추가해주면 ItemBookBinding과 연결됨. layout리소스파일의 camelCase이름 자동변환 인식.
          //상속받는 ViewHolder 생성자에는 꼭 binding.root를 전달해야 합니다.
          
          fun bind(bookModel: Book){ //북이라는 이름으로 북클래스를 가져옴
              binding.titleTextView.text = bookModel.title
              binding.descriptionTextView.text = bookModel.description

              Glide//글라이드를 통해 이미지 로딩
                  .with(binding.coverImageView)
                  .load(bookModel.coverSmallUrl)
                  .into(binding.coverImageView) //서버에서 url이미지를 가져와서 추가

          }
      }
       //그 후 ListAdapter 구현해야할 funtion 구현
      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder { //미리 만들어진 뷰 홀더가 없을 경우 새로 생성
          return BookItemViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context),parent,false))  //inflate인자로는 layoutinflater가 들어가게 되고 
          //뷰에도 컨텍스트가 있기에 parent 뷰에서 가져옴
      }

      override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) { //실제로 뷰 홀더가 뷰에 그려지게 됐을 때 데이터를 바인드해주는 함수
          holder.bind(currentList[position]) //데이터를 가져와서 bind함수를 통해서 값 바인드
      }
      //ListAdapter 사용 가능 메소드 참고)
      //getCurrentList() : 현재 리스트를 반환
      //onCurrentListChanged() : 리스트가 업데이트 되었을 때 실행할 콜백 지정
      //submitList(MutableList<T> list) : 리스트 데이터를 교체할 때 사용


      // diffUtil 리사이클러뷰가 실제로 뷰에 포지션이 변경되었을 때 새로운 값을 할당할지말지 기준
      //oldList와 newList 리스트의 차이를 계산하고 oldList를 newList로 변환하는 업데이트 작업 목록을 출력할 수 있는 유틸성 클래스이다.
      companion object{
          val diffUtil = object : DiffUtil.ItemCallback<Book>(){  //두 개의 메소드 구현// 두개의 기준을 보고 리사이클러뷰는 데이터를 업데이트 하는 기준을 가짐
              override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                  //아이템이 같으냐?
                  return oldItem == newItem
              }

              override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {

                  //컨텐츠가 같으냐?
                  return oldItem.id == newItem.id
              }

            }
        }

    }
    
    
    그 후 MainActivity에서 바인딩을 사용하기 위해서 다음과 같이 변경
    private lateinit var binding : ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) 액티비티 안에는 이미 layoutInflater가 존재
        //북서비스 실행 전에 북 어댑터 장착 & 매니저 생성
        val adapter = BookAdapter()
        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)//인자로는 context 
        binding.bookRecyclerView.adapter = adapter

        setContentView(binding.root)  //여기선 무조건 root를 넣어줘야함.
       
    위 리사이클러 사용 로직을 함수 추상화한다.
    fun initBookRecyclerView(){
        adapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this) //레이아웃매니저
        binding.bookRecyclerView.adapter = adapter//어댑터 장착
    }
    






### 도서 리스트 화면 - 도서 목록 보여주기
그려줄 아이템뷰를 꾸며줌-> 그 후 바인드 함수에서 값 바인드해줌
이미지 리소스 설정 url형식으로 되어있음(coverSmallUrl)은 다운받을 필요가 있다.
glidle을 이용.
**추가방법**
	implementation 'com.github.bumptech.glide:glide:4.12.0'
   	annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0' 
**사용방법** (바인드 bind함수에 추가)
	Glide//글라이드를 통해 이미지 로딩
                  .with(binding.coverImageView) 	//컨텍스트를 인자로 넣어줌
                  .load(bookModel.coverSmallUrl)
                  .into(binding.coverImageView) //서버에서 url이미지를 가져와서 추가
그러나 빌드를 하면 문제가 발생한다. 이미지로드가 되지않아서 로그를 보니 fail to laod ... 값이 http데이터 형식으로 내려온 상황이다.
아까는 baseUrl을 https로 변경을 해주었지만 이것은 실제로 값이 http로 데이터가 내려왔다. 하여 어쩔 수 없이 안드로이드에서 http를 허용시키겠다.
manifest의 application부분에서 usesCleartextTraffic을 true로 추가시켜주면 평문통신인 http를 허용시킨다. 그후 해결이 되었다.
	
	
	
### 도서 검색 페이지 - 도서 검색하기

### 도서 검색 페이지 - 검색 기록 저장하기
최근검색어를 DB에 저장하고 삭제하는 기능 추가

Android Room사용 예정(계산기 만들면서 실습해봄)


	**추가방법**
	id 'kotlin-kapt'
	
	implementation 'androidx.room:room-runtime:2.2.6'
   	 kapt 'androidx.room:room-compiler:2.2.6'
	 
**클래스 앱 데이터베이스를 만든다.AppDataBase -> 룸을 사용하기 위해서 @DataBase입력**	 

	@Database(entities = [History::class], version = 1)
	abstract class AppDatabase :RoomDatabase(){

	    //DB는 Dao에서 꺼내옴
	    abstract fun historyDao(): HistoryDao
	}
	
**Entity를 만들어준다 History**

	@Entity
	data class History (//모델클래스는 data클래스

	    @PrimaryKey val uid : Int?,
	    @ColumnInfo(name = "keyword") val keyword : String?,


	)
**DAO를 만들어준다.**(인터페이스)
(Data Access Object) 참고) DB를 사용해 데이터를 조화하거나 조작하는 기능을 전담
자신이 필요한 interface를 DAO에게 던지고 DAO는 이 인터페이스를 구현한 객체를 반환

	@Dao 
	interface HistoryDao {
	    @Query("SELECT * FROM history") //history테이블에서 모든 데이터를 가져옴
	    fun getAll(): List<History>

	    @Insert
	    fun insertHistory(history: History)

	    @Query("DELETE FROM history WHERE keyword == :keyword")
	    fun delete(keyword : String)

	}
	
MainActivity에서 전역변수로 DB를 구현

	private lateinit var db : AppDatabase
	db = Room.databaseBuilder(
            applicationContext,		//applicationContext가 인자로 들어감
            AppDatabase::class.java,	//데이터베이스 클래스가 인자로 들어감
            "BookSearchDB"		//이름
        ).build()			//db생성
	
search함수 호출 시 db저장할 것이므로 onResponse안에 saveSearchKeyword()함수로 저장

	private fun saveSearchKeyword(keyword : String){
		Thread {
		    db.historyDao().insertHistory(History(null, keyword))
		}.start()
	}
	
리사이클러뷰로 히스토리뷰 추가
그 후 히스토리 뷰는 숨겨졌다가 자유자재로 나와야하기에  function으로 정의

	private fun showHistoryView(){
		Thread{
		    val keywords = db.historyDao().getAll().reversed() //최신순서대로 가져옴

		    runOnUiThread(){//ui작업을 하기 위해서
			binding.historyRecyclerView.isVisible = true
			historyAdapter.submitList(keywords.orEmpty())	//히스토리 어댑터에 넣어줌.
		    }
		}
		binding.historyRecyclerView.isVisible = true //나타남
	    }
	    private fun hideHistoryView(){
		binding.historyRecyclerView.isVisible = false	//사라짐
	    }
	    
히스토리 어댑터에 값을 넣어주기위하여 어댑터를 만들어준다.
delete버튼을 활성화하기 위하여 HistoryAdapter() 생성자 부분에 인자 추가
	class HistoryAdapter(**val historyDeleteClickedListenr : (String)->Unit)** : ListAdapter<History, HistoryAdapter.HistoryItemViewHolder>(diffUtil) {
	**//String을 인자로 받는 데 return값은 없는함수를 타입으로 가지는 람다함수.**
	
	
initHistoryRecyclerView() 함수 정의

### 도서 상세 페이지 - 도서 상세 보여주기
액티비티 추가 후 


값을 MainActivity로 부터 가져와야함. (Intent를 통해서 가져옴)
리사이클러뷰에 있는 데이터를 가져와야하기에 클릭리스너를 붙여주어야함.
어댑터에 붙여줌. -> initBookReycyclerView()안에 BookAdapter안에 클릭리스너를 인자로 넣어준다.

	class BookAdapter(private val itemClickedListener : (Book) -> Unit) :
	
	fun bind(bookModel: Book){ 
            
            binding.root.setOnClickListener{ //루트를 클릭하게 된다면 함수 호출
                itemClickedListener(bookModel)// 이함수는 MainActivity에서 initRecyclerView안에서 구현
            }
	    
MainActivity에서 initRecyclerView안에서 구현

	fun initBookRecyclerView(){
		adapter = BookAdapter(itemClickedListener = {
		    val intent = Intent(this, DetailActivity::class.java)
		    //클래스를 직렬화 시켜서 클래스 자체를 넘기는 방법
		    intent.putExtra("bookModel", it)
		    startActivity(intent)})
북 클래스를 직렬화 하기 위해서는
	
	id 'kotlin-parcelize' 
추가 후에 Book 클래스에 어노테이션추가 후 직렬화가능 선언

	@Parcelize
	data class Book(
	    @SerializedName("itemId") val id : Long,
	    @SerializedName("title") val title : String,
	    @SerializedName("description") val description : String,
	    @SerializedName("coverSmallUrl") val coverSmallUrl : String
	) : Parcelable //직렬화가 가능하게 선언

액티비티 추가 후 manifest 추가 주의

### 어떤 것을 추가로 개발할 수 있을까?
	DAO를 두개 사용하니 크러시가 났다. 
	DB가 이미 생성이 됐는데 DB룰이 들어왔을 때 migration이 안 돼서 그렇다. 그럴 땐 앱을 삭제하고 다시 설치하는 것으로 대처
룸에서는 migration코드를 필요로한다. 
	
	
	
### 아웃트로   

->이 챕터를 통해 배우는 것
    RecyclerView 사용하기
    View Binding 사용하기 -> findViewByID 최적화 https://developer.android.com/topic/libraries/view-binding?hl=ko
      안드로이드 4.0 부터는 data binding 과 view binding 을 사용하기 위해서는 사용 선언 방법이 조금 변경 되었다고 합니다.
        android {
            buildFeatures {
                  viewBinding = true
             }
        }

	Retrofit 사용하기 (API 호출)
	Glide 사용하기 (이미지 로딩)
	Android Room 사용하기 (복습 파트2, 챕터4 계산기, Local DB)
	Open API 사용해보기



	
  
  
