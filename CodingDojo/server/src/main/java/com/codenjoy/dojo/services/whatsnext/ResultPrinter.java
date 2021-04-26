package com.codenjoy.dojo.services.whatsnext;

import com.codenjoy.dojo.services.multiplayer.Single;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.services.whatsnext.WhatsNextService.countFromOne;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;

public class ResultPrinter {

    private int maxLength;
    private StringBuilder result;

    public ResultPrinter(int boardSize) {
        maxLength = boardSize + 12;
        result = new StringBuilder();
    }

    public void printBoard(List<Info> infos, List<Single> singles) {
        for (int index = 0; index < singles.size(); index++) {
            Single single = singles.get(index);
            Info info = infos.get(index);
            String boardInfo = String.format(
                    "Board:\n%s" +
                            "Events:%s\n",
                    single.getBoardAsString().toString(),
                    info.all().toString()
            );
            result.append(formatSpaces(index, boardInfo));
        }
        result.append("|").append(repeat(' ', maxLength - 1)).append("\n");
    }

    // TODO to use common StringBuilder here
    private String formatSpaces(int index, String result) {
        List<String> lines = new ArrayList<>();
        lines.addAll(Arrays.asList(result.split("\n")));

        String prefix = String.format("| (%s) ", countFromOne(index));
        lines = lines.stream()
                .map(line -> prefix + line)
                .collect(toList());

        lines.add(0, "|");

        lines = lines.stream()
                .map(line -> line + repeat(' ', maxLength - line.length()))
                .collect(toList());

        return lines.stream()
                .collect(joining("\n")) + "\n";
    }

    public void printTickHeader(int tick) {
        printHeader("tick " + countFromOne(tick));
    }

    public void printInitialHeader() {
        printHeader("setup ");
    }

    private void printHeader(String tickInfo) {
        int spacesLength = (maxLength - tickInfo.length()) / 2;
        String spaces = repeat(' ', spacesLength);
        breakLine();
        result.append(String.format("|%s%s%s\n", spaces, tickInfo, spaces));
        breakLine();
    }

    public void breakLine() {
        result.append("+").append(repeat('-', maxLength - 1)).append("\n");
    }

    @Override
    public String toString() {
        return result.toString();
    }
}