package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.Entity;

public interface FriendshipRepoPage<ID,E extends Entity<ID>> extends PagingRepository<ID,E> {
    Page<E> findAllFriendships(Pageable pageable) ;

    Page<E> findAllUserFriends(Pageable pageable,Long userId) ;

    Page<E> findAllUserFriendRequests(Pageable pageable,Long userId) ;
}
