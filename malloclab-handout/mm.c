/*
 * mm-implicit.c - an empty malloc package
 *
 * NOTE TO STUDENTS: Replace this header comment with your own header
 * comment that gives a high level description of your solution.
 *
 * @id : 201502127 
 * @name : 허세훈
 */
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "mm.h"
#include "memlib.h"

/* If you want debugging output, use the following macro.  When you hand
 * in, remove the #define DEBUG line. */
#define DEBUG
#ifdef DEBUG
# define dbg_printf(...) printf(__VA_ARGS__)
#else
# define dbg_printf(...)
#endif


/* do not change the following! */
#ifdef DRIVER
/* create aliases for driver tests */
#define malloc mm_malloc
#define free mm_free
#define realloc mm_realloc
#define calloc mm_calloc
#endif /* def DRIVER */

/* single word (4) or double word (8) alignment */
//프로그램 상에서 16byte씩 한 블럭으로 치고 있는거 같다.
//=> 즉, payload에 해당하는게 8byte이고 header와 footer를 합쳐서 8byte => 총 16byte(4word)
#define ALIGNMENT 8 //실제 데이터를 위해 8씩 할당 받을 수 있다.
#define WSIZE 4 //single word size = 4byte
#define DSIZE 8 //double word size = 8byte
#define CHUNKSIZE (1 << 12) //초기heap size = 4096byte
#define OVERHEAD 8 //header(1word) + footer(1word) = 8byte(2word) => footer와 header를 위해 8byte를 할당해주어야 함
#define MAX(x, y) ((x) > (y) ? (x) : (y)) //x와 y중에 큰 값을 반환
#define PACK(size, alloc) ((size) | (alloc)) //header와 footer의 size부분과 alloc부분의 비트를 합쳐 하나의 word로 만들어줌 
// => header와 footer를 만들 수 있다.
#define GET(p) (*(unsigned int*)(p)) //포인터 p가 가리키는 곳에 있는 4word만큼의 공간의 값을 읽어온다.
#define PUT(p, val) (*(unsigned int*)(p) = (val)) //포인터 p가 가리키는 곳에 있는 4word만큼의 공간에서 val값을 쓴다. => 그 공간에 값을 넣는 setter 역할이라 생각하면 될듯.

/* rounds up to the nearest multiple of ALIGNMENT */
#define ALIGN(p) (((size_t)(p) + (ALIGNMENT-1)) & ~0x7)
#define GET_SIZE(p) (GET(p) & ~0x7) //header를 읽어와서 block size를 알아낼 수 있다. => header의 주소를 알아내서 값을 얻어오고 size에 해당하는 부분만 가져온다.
#define GET_ALLOC(p) (GET(p) & 0x1) //alloc 여부를 알 수 있다.=> heade의 주소를 알아내서 값을 얻어오고 alloc에 해당하는 부분만 가져온다.
#define HDRP(bp) ((char*)(bp) - WSIZE) //header의 주소
#define FTRP(bp) ((char*)(bp) + GET_SIZE(HDRP(bp)) - DSIZE) //footer의 주소
#define NEXT_BLKP(bp) ((char*)(bp) + GET_SIZE((char*)(bp) - WSIZE)) //다음 block의 주소를 계산해준다.
#define PREV_BLKP(bp) ((char*)(bp) - GET_SIZE((char*)(bp) - DSIZE)) //이전 block의 주소를 계산해준다.

static char *heap_listp = 0;

static void *extend_heap(size_t words);
static void *find_fit(size_t asize);
static void place(void *bp, size_t asize);
static void *coalesce(void *bp);

/*
 * Initialize: return -1 on error, 0 on success.
 */
int mm_init(void) {
	if((heap_listp = mem_sbrk(4 * WSIZE)) == NULL) {
		return -1;
	}
	//16바이트를 heap에 할당한다.
	//초기 생성하는 블록이 16바이트를 가지는데 이게 null이라는 뜻은 할당 실패했다는 말이니까 실패

	PUT(heap_listp, 0); //힢 공간의 첫번째 위치를 0으로 지정한다.
	PUT(heap_listp + WSIZE, PACK(OVERHEAD, 1)); //프롤로그의 헤더 => 할당된것으로 바꿔줌
	PUT(heap_listp + DSIZE, PACK(OVERHEAD, 1)); //프롤로그의 footer => 할당된것으로 바꿔줌
	PUT(heap_listp + WSIZE + DSIZE, PACK(0, 1)); //에필로그의 헤더 => 할당된것으로 바꿔줌
	heap_listp += DSIZE; //HEAP_LISTP의 위치가 header와 footer 사이 즉, 실제 데이터가 들어가는 위치가 된다.

	if((extend_heap(CHUNKSIZE / WSIZE)) == NULL)
		return -1;
		//CHUNKSIZE만큼 HEAP을 확장시킨다. => 즉, 초기 HEAP이 4096BYTE의 크기를 갖게  되는 것이다.(extend_heap의 파라미터가 word이므로 byte단위를 word단위로 변환해서 넘겨준다.) => 1024word가 됨 => NULL이면 실패이므로 -1 반환

	return 0; //성공했으므로 0 반환
}


//힙을 CHUNKSIZE 바이트로 확장하고 초기 가용 블록을 생성한다.
static void *extend_heap(size_t words) {
	//파라미터로 word사이즈만큼을 받는다.
	char *bp;
	size_t size;

	size = (words % 2) ? ((words + 1) * WSIZE) : (words * WSIZE); //짝수로 size를 지정한다. => 즉, 우리는 짝수 단위로 힢을 확장한다.

	if((long)(bp = mem_sbrk(size)) == -1)
		return NULL;
	//확장하기 실패했다면 null반환

	PUT(HDRP(bp), PACK(size, 0)); //새로운 프롤로그의 헤더  설정
	PUT(FTRP(bp), PACK(size, 0)); //새로운 프롤로그의 footer 설정
	PUT(HDRP(NEXT_BLKP(bp)), PACK(0, 1)); //새로운 에필로그 설정

	return coalesce(bp); //앞의 힢과 합쳐서 반환한다.

}


/*
 * malloc
 */
void *malloc (size_t size) {
	
	size_t asize; //할당받을 블록의 size이다.
	size_t extendsize; //힙을 더 늘리기 위해 증가시킬 size이다.
	char *bp;

	if(size == 0)
		return NULL; //size가 0이므로할당 할 필요 없기에  아무것도 하지 않는다.

	if(size <= DSIZE)
		asize = 2*DSIZE; //size가 8보다 작다는 이야기는 payload가 8단위가 아니라는 의미이다. 따라서, 우리가 의도한 16단위의 블록이 아니다. 따라서, 16단위의 블록으로 맞게 할당받을 size를바꿔준다. 또한, payload가 8이랑 같다면 asize를 16만큼으로 바꾸어 블록을 16단위로 할당시킨다.
	else
		asize = DSIZE * ((size + (DSIZE) + (DSIZE - 1)) / DSIZE); //size 조정시켜서 16단위로 할당할 수 있게 해줌 => 모든 경우가 걸린다 이 한 줄에!

	//말록할 수 있는 곳을 탐색하러 간다.
	if((bp = find_fit(asize)) != NULL) {
		place(bp, asize); //할당할 수 있는 곳 찾아서 쪼갠다.
		return bp;
	}

	extendsize = MAX(asize, CHUNKSIZE);
	//CHUNKSIZE만큼을 보통의 경우 확장하지만 그거보다 더 큰 공간을 할당받아야한다면 asize만큼 확장한다.

	if((bp = extend_heap(extendsize / WSIZE)) == NULL)
		return NULL;
	//힢을 확장시킨다.

	place(bp, asize);
	//새로 확장된 곳에 블럭을 쪼개서 넣는다.
	return bp;
	
}

//asize와 같거나 큰 할당되지 않은 블록(free 블록)을 찾는다.
static void *find_fit(size_t asize) {
	void *bp;

	bp = NEXT_BLKP(heap_listp); //프롤로그 블록을 피하기 위해 처음 주소 다음 것을 가리킨다.

	//처음부터 순차적으로 탐색한다 => first fit방식
	while(GET_SIZE(HDRP(bp)) > 0 ) {

		if(!GET_ALLOC(HDRP(bp)) && (asize <= GET_SIZE(HDRP(bp)))) { 
			//할당되지 않았거나 size를 맞춰줄 수 있다면 그 자리에 넣을 수 있기에 그 자리를 반환한다.
			return bp;
		}

		bp = NEXT_BLKP(bp); //다음 블록으로 가본다.
	}

	return NULL; //실패했기에 null을 반환한다.
}

//bp 포인터의 위치에 해당하는 곳에서 파라미터로 받은 size로 블록을 쪼개어주는 역할을 하는 함수
static void place(void *bp, size_t asize) {

	size_t remainSize = GET_SIZE(HDRP(bp)); //힢에서 남은 공간 중에 실제로 할당할 공간의 size를  받아온다.
	size_t freeSize = remainSize - asize; //할당하고 난 뒤에 free블럭의 size

	//남은 공간이 할당받을 공간보다 커서 쪼갤 수 있는 경우
	if((2 * DSIZE) <= (freeSize)) {
		PUT(HDRP(bp), PACK(asize, 1));
		PUT(FTRP(bp), PACK(asize, 1));
		bp = NEXT_BLKP(bp); //다음 블럭으로 옮긴다.
		
		//다음 블럭 처리를 해준다. => free블럭의 size를 처리해주면 됨!
		PUT(HDRP(bp), PACK(freeSize, 0));
		PUT(FTRP(bp), PACK(freeSize, 0));
	}
	else {
		//쪼갤 수 없으므로 그냥 할당받은 공간에 바로 size처리 해준다.
		PUT(HDRP(bp), PACK(remainSize, 1));
		PUT(FTRP(bp), PACK(remainSize, 1));
	}
}


/*
 * free
 */
void free (void *ptr) {
    if(ptr == 0) return; //블럭이 정상적이지 않아서 주소값이 이상하다면 함수 종료(주소가 0이다?)
	size_t size = GET_SIZE(HDRP(ptr)); //block의 size를 알아냄

	//현재 free시키기 위한 블럭의 정보들을 합친다. pack으로 합쳐서 각각을 header와 footer에 저장한다.
	PUT(HDRP(ptr), PACK(size, 0)); //할당이 안 되었으므로 alloc은 0이 된다.
	PUT(FTRP(ptr), PACK(size, 0)); //할당이 안 되었으므로 alloc은 0이 된다.

	coalesce(ptr); //인접한 블럭들이 free상태라면 합친다.
}

/*
 * realloc - you may want to look at mm-naive.c
 */
void *realloc(void *oldptr, size_t size) {
    size_t oldsize;
	void *newptr;

	if(size == 0) {
		free(oldptr);
		return 0;
	}

	if(oldptr == NULL) {
		return malloc(size);
	}

	newptr = malloc(size);

	if(!newptr) {
		return 0;
	}

	oldsize = GET_SIZE(HDRP(oldptr));
	if(size < oldsize) oldsize = size;
	memcpy(newptr, oldptr, oldsize);

	free(oldptr);
	
	return newptr;
}

/*
 * calloc - you may want to look at mm-naive.c
 * This function is not tested by mdriver, but it is
 * needed to run the traces.
 */
void *calloc(size_t nmemb, size_t size) {
	size_t bytes = nmemb * size;
	void *newptr;

	newptr = malloc(bytes);
	memset(newptr, 0, bytes);

    return newptr;
}


/*
 * Return whether the pointer is in the heap.
 * May be useful for debugging.
 */
static int in_heap(const void *p) {
    return p < mem_heap_hi() && p >= mem_heap_lo();
}

/*
 * Return whether the pointer is aligned.
 * May be useful for debugging.
 */
static int aligned(const void *p) {
    return (size_t)ALIGN(p) == (size_t)p;
}

/*
 * mm_checkheap
 */
void mm_checkheap(int verbose) {
}

/* 인접한 free블록을 합쳐주는 함수 */
static void *coalesce(void *bp) {
	size_t prev_alloc = GET_ALLOC(FTRP(PREV_BLKP(bp)));

	size_t next_alloc = GET_ALLOC(HDRP(NEXT_BLKP(bp)));

	size_t size = GET_SIZE(HDRP(bp));
	//현재 블럭의 header 값을 얻어옴으로써 현재 블럭의 size를 알아낸다.

	//양 옆의 블록들이 모두 할당되어있으므로 합쳐줄 필요없다.
	if(prev_alloc && next_alloc) {
		return bp;
	}

	//이전 블럭은 할당되어 있지만 다음 블럭은 할당되어 있지 않은 경우 => 다음블럭과 현재블럭을 합친다.
	else if(prev_alloc && !next_alloc) {
		size += GET_SIZE(HDRP(NEXT_BLKP(bp)));
		PUT(HDRP(bp), PACK(size, 0));
		PUT(FTRP(bp), PACK(size, 0));
	}

	//이전 블럭은 할당되어 있지 않지만 다음 블럭은 할당되어 있는 경우 => 이전블럭과 현재블럭을 합친다. 이때, 이전 블럭이 더 앞에 있기에 새로 합쳐진 블럭의 header는 이전블럭의 header가 됨에 유의해야한다.=> footer는 현재  블럭이 된다.  => 또한, 반환해줄 포인터도 이전 블럭으로 변경해주어야 한다.
	else if(!prev_alloc && next_alloc) {
		size += GET_SIZE(HDRP(PREV_BLKP(bp)));
		
		PUT(HDRP(PREV_BLKP(bp)), PACK(size, 0));
		PUT(FTRP(bp), PACK(size, 0));

		bp = PREV_BLKP(bp);
	}

	//양 옆의 블럭 모두 할당되어 있지 않은 경우 => 일단 최종사이즈를 구한다. => 양 옆의 블럭을 합친것이 최종 블럭의 size가 된다. => 최종 SIZE를 이전 블럭에 갱신해주고 포인터 바꿔주면된다. => 내가 잘 못 알고 있던 부분이 있는데 합쳐질때 마지막 블럭이 footer를 가지고 있는다!! => 합쳐지니까 맨 끝이 footer가 된다..
	else {
		size += GET_SIZE(HDRP(PREV_BLKP(bp)));
		size += GET_SIZE(HDRP(NEXT_BLKP(bp)));

		PUT(HDRP(PREV_BLKP(bp)), PACK(size, 0));
		PUT(FTRP(NEXT_BLKP(bp)), PACK(size, 0));

		bp = PREV_BLKP(bp);
	}

	return bp;

}

