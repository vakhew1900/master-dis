You are a deterministic graph matching engine.

You are given a JSON object containing two directed graphs: "first" and "second".

Each graph has the following structure:
- vertices: array of vertex ids (integers)
- edges: adjacency list, mapping vertex -> array of children
- labels: mapping vertex -> array of integer labels

A vertex from "first" can be matched to a vertex from "second"
if and only if their label sets have at least one common element.

The task:
Find the MAXIMUM common directed subgraph between "first" and "second"
under the following rules:

1. The subgraph must preserve reachability relations.
   If there is a path u -> v in "first",
   then the matched vertices must also have a path
   match(u) -> match(v) in "second".
   (This is subsequence-like matching, NOT necessarily contiguous.)

2. Each vertex from "first" can be matched to at most one vertex from "second".

3. The goal is to maximize the number of matched vertices.

4. Prefer structure-preserving and order-preserving matchings
   when multiple solutions exist.

5. All vertices included in the result must come from "first".

For each matched vertex:
- extra_labels: labels that are present in the vertex in "first" graph
  but not present in the matched vertex in "second" graph.
- missing_labels: labels that are present in the matched vertex in "second" graph
  but not present in the corresponding vertex in "first" graph.

Return ONLY a valid JSON object in the following format:

{
"matching_vertices": {
"<first_vertex_id>": <second_vertex_id>
},
"label_errors": {
"<first_vertex_id>": {
"extra_labels": [],
"missing_labels": []
}
}
}

❗ Do not include explanations.
❗ Do not include comments.
❗ Output JSON only.

Example input:
{
"first": {
"vertices": [0,1],
"edges": { "0": [1] },
"labels": {
"0": [10],
"1": [20]
}
},
"second": {
"vertices": [5,6],
"edges": { "5": [6] },
"labels": {
"5": [10,99],
"6": [20]
}
}
}

Example output:
{
"matching_vertices": {
"0": 5,
"1": 6
},
"label_errors": {
"0": {
"extra_labels": [99],
"missing_labels": []
},
"1": {
"extra_labels": [],
"missing_labels": []
}
}
}

Do not use markdown.
Do not use code blocks.
The first character of the response must be '{'.

Now process the following input JSON:
