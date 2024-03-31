## CommerceApp

MockServer를 이용하여 커머스앱 Main화면에 필요한 Section 데이터와 Product 데이터를 받아 화면에 그려줍니다. 사용자는 ProductCard의 오른쪽 상단 찜하기 버튼을 클릭하여 항목을 표시해둘 수 있습니다.

### 구성 : <br>
- Kotlin
- Coroutine
- Compose
- Hilt
- MVVM
- SharedPreferecne
- Retrofit2, OkHttp3
- Coil

<br>
<hr>
<br>



## Compose 작성 중 특이사항
<br>

### 1. Grid section
요구사항에 맞는 이미지(width = 150dp, height = 200dp)로 구성할 경우 항목이 겹치거나 어떤 영역이 잘리는 화면이 나올 수 있고(2*3 화면이기 때문에 화면의 너비가 450dp가 안되는 경우 잘릴 수 밖에 없음), 사용자의 편의성에 문제가 될 것이라 생각되어 scrollable하게 작성하였습니다.

### 2. ProductCard
제품카드 ui를 하나의 ProductCard로 두고 style값을 넣어 처리할 지 두 개의 ProductCard로 넣어 활용할지 고민 후, VerticalProductCard를 추가하여 작성하였습니다.<br>
ProductCard의 데이터 노출에 대해 style을 넣어 처리할 경우 너무 많은 분기처리가 생길 것이라 판단하였고, 이후에도 각 컴포넌트별 요구사항이 변경 및 추가될 때마다 새로운 분기처리의 가능성이 있어 따로 컴포넌트를 구성하였습니다.

### 3. Price
제품의 가격 노출 정책을 기준으로 화면을 구성하기 위해 Product model에 
```
 fun getSalesStatus(): SalesStatus {
        if (isSoldOut) return SalesStatus.SOLD_OUT
        if (discountedPrice != null) return SalesStatus.ON_DISCOUNT
        return SalesStatus.ON_SALE
    }
```
함수를 추가하였습니다. <br> Product의 가격 노출 정책을 domain model에서 바로 호출해서 처리할 수 있도록 작성하였습니다.
<br>
<br>
<br>

## 내부 DB(SharedPreference)
서버 작업 없이 찜하기 기능을 수행할 수 있도록 간단한 내부DB를 사용하였습니다. <br>찜 리스트를 전체 호출하는 것 이외에도 insert, delete의 호출에 따라 찜 리스트의 데이터가 변경되고 그것을 viewModel이 인지하는 것이 화면 작업에 유리할 것이라 여겨 flow로 데이터 스트림을 계속해서 넘겨받을 수 있도록 작업하였습니다.


<hr>
<br>
<br>

상대적으로 오래 걸리는 작업(api 호출 등)에 대해 async, await을 사용하여 병렬 호출 후 데이터를 사용, 응답 결과를 받는 시간을 최적화하기 위해 노력했습니다.

<br>

## 실험. 실제로 병렬 호출했을 때 소요시간이 감소했는가?

가설: 순차적으로 api 호출보다 병렬적으로 api 호출이 소요시간이 더 적을 것이다.


1. 순차적으로 api 데이터 호출
```
class GetSectionsWithProductsUseCase @Inject constructor(
    private val sectionRepository: MainSectionRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(page: Int): Flow<Response<Sections>> = flow {
        try {
            emit(Response.Loading)
            val duration = measureTimedValue {
                val sections = sectionRepository.getSections(page)
                val likeProducts = productRepository.getAllLikeProducts().first()
                withContext(Dispatchers.IO) {
                    sections.sectionInfoList.forEach { sectionInfo ->
                        val products = sectionRepository.getSectionProducts(sectionInfo.id)
                        sectionInfo.products =  products.map {
                            it.copy(isLike = likeProducts.contains(it))
                        }
                    }
                }
                emit(Response.Success(sections))
            }
            println("Execution Time: ${duration.duration.inWholeMilliseconds} milliseconds")
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }
}
```
동작 소요시간
> Execution Time: 13384 milliseconds <br>
Execution Time: 11848 milliseconds<br>
Execution Time: 9702 milliseconds<br>
Execution Time: 13059 milliseconds<br>
Execution Time: 9923 milliseconds<br>
Execution Time: 11847 milliseconds<br>
Execution Time: 10168 milliseconds<br>
Execution Time: 11835 milliseconds<br>
Execution Time: 12021 milliseconds<br>
> 평균 소요 시간은 11543 milliseconds

2. 병렬적으로 api 데이터 호출 

```
class GetSectionsWithProductsUseCase @Inject constructor(
    private val sectionRepository: MainSectionRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(page: Int): Flow<Response<Sections>> = flow {
        try {
            emit(Response.Loading)
            val duration = measureTimedValue {
                val sections = sectionRepository.getSections(page)
                val likeProducts = productRepository.getAllLikeProducts().first()
                withContext(Dispatchers.IO) {
                    val deferredList = sections.sectionInfoList.map { sectionInfo ->
                        async {
                            sectionRepository.getSectionProducts(sectionInfo.id).map {
                                it.copy(isLike = likeProducts.contains(it))
                            }
                        }
                    }

                    val productList = deferredList.awaitAll()
                    sections.sectionInfoList.mapIndexed { index, sectionInfo ->
                        sectionInfo.products = productList[index]
                    }
                }
                emit(Response.Success(sections))
            }
            println("Execution Time: ${duration.duration.inWholeMilliseconds} milliseconds")
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }
}
```

동작 소요시간
>Execution Time: 4027 milliseconds<br>
Execution Time: 3767 milliseconds<br>
Execution Time: 3719 milliseconds<br>
Execution Time: 3885 milliseconds<br>
Execution Time: 4811 milliseconds<br>
Execution Time: 3735 milliseconds<br>
Execution Time: 4799 milliseconds<br>
Execution Time: 3652 milliseconds<br>
Execution Time: 3619 milliseconds<br>
>평균 소요시간 3990 milliseconds


## 결론
병렬로 데이터를 호출했을 때, 순차적으로 호출한 코드보다 소요시간이 더 짧은 것을 확인할 수 있었습니다.<br>
따라서<br> 
> sections api 호출 -> sectionList 응답 받은 후 응답 기준 list의 productList를 병렬로 호출(async, await) -> 결과값 emit<br>

의 방식으로 동작하였을 때 순차 호출 대비, 평균 대략 7.5초의 시간을 단축시킬 수 있었습니다.



