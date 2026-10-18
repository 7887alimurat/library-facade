package library.subsystem;

import java.util.HashMap;
import java.util.Map;

/** Knows who is registered in the library. */
public class MemberRegistry {
    private final Map<String, Member> members = new HashMap<>();

    public void register(String id, String name) {
        if (members.containsKey(id)) {
            throw new IllegalArgumentException("Member id already used: " + id);
        }
        members.put(id, new Member(id, name));
    }

    public Member findById(String id) {
        Member member = members.get(id);
        if (member == null) {
            throw new IllegalArgumentException("Member not found: " + id);
        }
        return member;
    }
}
