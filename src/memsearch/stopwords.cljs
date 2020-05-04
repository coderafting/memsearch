(ns memsearch.stopwords)

;; TODOs:
;; 1. Option to provides weights to words, in addition to stop-words
;; 2. Add more words to stop-words list
; http://www.lextek.com/manuals/onix/stopwords1.html
(def temp
  ["a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any","are","aren't","as","at","be","because","been","before","being","below","between","both","but","by","can't","cannot","could","couldn't","did","didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from","further","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her","here","here's","hers","herself","him","himself","his","how","how's","i","i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's","its","itself","let's","me","more","most","mustn't","my","myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours","ourselves","out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such","than","that","that's","the","their","theirs","them","themselves","then","there","there's","these","they","they'd","they'll","they're","they've","this","those","through","to","too","under","until","up","very","was","wasn't","we","we'd","we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's","which","while","who","who's","whom","why","why's","with","won't","would","wouldn't","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves"])

(def temp2
  ['i','me','my','myself','we','our','ours','ourselves','you','your','yours','yourself','yourselves','he','him','his','himself','she','her','hers','herself','it','its','itself','they','them','their','theirs','themselves','what','which','who','whom','this','that','these','those','am','is','are','was','were','be','been','being','have','has','had','having','do','does','did','doing','a','an','the','and','but','if','or','because','as','until','while','of','at','by','for','with','about','against','between','into','through','during','before','after','above','below','to','from','up','down','in','out','on','off','over','under','again','further','then','once','here','there','when','where','why','how','all','any','both','each','few','more','most','other','some','such','no','nor','not','only','own','same','so','than','too','very','s','t','can','will','just','don','should','now'])

(def temp3
  ['a'
'about'
'above'
'across'
'after'
'again'
'against'
'all'
'almost'
'alone'
'along'
'already'
'also'
'although'
'always'
'among'
'an'
'and'
'another'
'any'
'anybody'
'anyone'
'anything'
'anywhere'
'are'
'area'
'areas'
'around'
'as'
'ask'
'asked'
'asking'
'asks'
'at'
'away'
'b'
'back'
'backed'
'backing'
'backs'
'be'
'became'
'because'
'become'
'becomes'
'been'
'before'
'began'
'behind'
'being'
'beings'
'best'
'better'
'between'
'big'
'both'
'but'
'by'
'c'
'came'
'can'
'cannot'
'case'
'cases'
'certain'
'certainly'
'clear'
'clearly'
'come'
'could'
'd'
'did'
'differ'
'different'
'differently'
'do'
'does'
'done'
'down'
'down'
'downed'
'downing'
'downs'
'during'
'e'
'each'
'early'
'either'
'end'
'ended'
'ending'
'ends'
'enough'
'even'
'evenly'
'ever'
'every'
'everybody'
'everyone'
'everything'
'everywhere'
'f'
'face'
'faces'
'fact'
'facts'
'far'
'felt'
'few'
'find'
'finds'
'first'
'for'
'four'
'from'
'full'
'fully'
'further'
'furthered'
'furthering'
'furthers'
'g'
'gave'
'general'
'generally'
'get'
'gets'
'give'
'given'
'gives'
'go'
'going'
'good'
'goods'
'got'
'great'
'greater'
'greatest'
'group'
'grouped'
'grouping'
'groups'
'h'
'had'
'has'
'have'
'having'
'he'
'her'
'here'
'herself'
'high'
'high'
'high'
'higher'
'highest'
'him'
'himself'
'his'
'how'
'however'
'i'
'if'
'important'
'in'
'interest'
'interested'
'interesting'
'interests'
'into'
'is'
'it'
'its'
'itself'
'j'
'just'
'k'
'keep'
'keeps'
'kind'
'knew'
'know'
'known'
'knows'
'l'
'large'
'largely'
'last'
'later'
'latest'
'least'
'less'
'let'
'lets'
'like'
'likely'
'long'
'longer'
'longest'
'm'
'made'
'make'
'making'
'man'
'many'
'may'
'me'
'member'
'members'
'men'
'might'
'more'
'most'
'mostly'
'mr'
'mrs'
'much'
'must'
'my'
'myself'
'n'
'necessary'
'need'
'needed'
'needing'
'needs'
'never'
'new'
'new'
'newer'
'newest'
'next'
'no'
'nobody'
'non'
'noone'
'not'
'nothing'
'now'
'nowhere'
'number'
'numbers'
'o'
'of'
'off'
'often'
'old'
'older'
'oldest'
'on'
'once'
'one'
'only'
'open'
'opened'
'opening'
'opens'
'or'
'order'
'ordered'
'ordering'
'orders'
'other'
'others'
'our'
'out'
'over'
'p'
'part'
'parted'
'parting'
'parts'
'per'
'perhaps'
'place'
'places'
'point'
'pointed'
'pointing'
'points'
'possible'
'present'
'presented'
'presenting'
'presents'
'problem'
'problems'
'put'
'puts'
'q'
'quite'
'r'
'rather'
'really'
'right'
'right'
'room'
'rooms'
's'
'said'
'same'
'saw'
'say'
'says'
'second'
'seconds'
'see'
'seem'
'seemed'
'seeming'
'seems'
'sees'
'several'
'shall'
'she'
'should'
'show'
'showed'
'showing'
'shows'
'side'
'sides'
'since'
'small'
'smaller'
'smallest'
'so'
'some'
'somebody'
'someone'
'something'
'somewhere'
'state'
'states'
'still'
'still'
'such'
'sure'
't'
'take'
'taken'
'than'
'that'
'the'
'their'
'them'
'then'
'there'
'therefore'
'these'
'they'
'thing'
'things'
'think'
'thinks'
'this'
'those'
'though'
'thought'
'thoughts'
'three'
'through'
'thus'
'to'
'today'
'together'
'too'
'took'
'toward'
'turn'
'turned'
'turning'
'turns'
'two'
'u'
'under'
'until'
'up'
'upon'
'us'
'use'
'used'
'uses'
'v'
'very'
'w'
'want'
'wanted'
'wanting'
'wants'
'was'
'way'
'ways'
'we'
'well'
'wells'
'went'
'were'
'what'
'when'
'where'
'whether'
'which'
'while'
'who'
'whole'
'whose'
'why'
'will'
'with'
'within'
'without'
'work'
'worked'
'working'
'works'
'would'
'x'
'y'
'year'
'years'
'yet'
'you'
'young'
'younger'
'youngest'
'your'
'yours'
'z'])

(def stop-words
  {"a" true
   "about" true
   "above" true
   "after" true
   "again" true
   "against" true
   "all" true
   "am" true
   "an" true
   "and" true
   "any" true
   "are" true
   "aren't" true
   "as" true
   "at" true
   "be" true
   "because" true
   "been" true
   "before" true
   "being" true
   "below" true
   "between" true
   "both" true
   "but" true
   "by" true
   "can't" true
   "cannot" true
   "could" true
   "couldn't" true
   "did" true
   "didn't" true
   "do" true
   "does" true
   "doesn't" true
   "doing" true
   "don't" true
   "down" true
   "during" true
   "each" true
   "few" true
   "for" true
   "from" true
   "further" true
   "had" true
   "hadn't" true
   "has" true
   "hasn't" true
   "have" true
   "haven't" true
   "having" true
   "he" true
   "he'd" true
   "he'll" true
   "he's" true
   "her" true
   "here" true
   "here's" true
   "hers" true
   "herself" true
   "him" true
   "himself" true
   "his" true
   "how" true
   "how's" true
   "i" true
   "i'd" true
   "i'll" true
   "i'm" true
   "i've" true
   "if" true
   "in" true
   "into" true
   "is" true
   "isn't" true
   "it" true
   "it's" true
   "its" true
   "itself" true
   "let's" true
   "me" true
   "more" true
   "most" true
   "mustn't" true
   "my" true
   "myself" true
   "no" true
   "nor" true
   "not" true
   "of" true
   "off" true
   "on" true
   "once" true
   "only" true
   "or" true
   "other" true
   "ought" true
   "our" true
   "ours" true
   "ourselves" true
   "out" true
   "over" true
   "own" true
   "same" true
   "shan't" true
   "she" true
   "she'd" true
   "she'll" true
   "she's" true
   "should" true
   "shouldn't" true
   "so" true
   "some" true
   "such" true
   "than" true
   "that" true
   "that's" true
   "the" true
   "their" true
   "theirs" true
   "them" true
   "themselves" true
   "then" true
   "there" true
   "there's" true
   "these" true
   "they" true
   "they'd" true
   "they'll" true
   "they're" true
   "they've" true
   "this" true
   "those" true
   "through" true
   "to" true
   "too" true
   "under" true
   "until" true
   "up" true
   "very" true
   "was" true
   "wasn't" true
   "we" true
   "we'd" true
   "we'll" true
   "we're" true
   "we've" true
   "were" true
   "weren't" true
   "what" true
   "what's" true
   "when" true
   "when's" true
   "where" true
   "where's" true
   "which" true
   "while" true
   "who" true
   "who's" true
   "whom" true
   "why" true
   "why's" true
   "with" true
   "won't" true
   "would" true
   "wouldn't" true
   "you" true
   "you'd" true
   "you'll" true
   "you're" true
   "you've" true
   "your" true
   "yours" true
   "yourself" true
   "yourselves" true})