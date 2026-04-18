package utility.loader;


record StateTransition(
        ParserState prev_state, String qName, ParserState new_state) {
}
